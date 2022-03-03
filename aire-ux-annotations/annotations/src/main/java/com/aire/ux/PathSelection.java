package com.aire.ux;

import static com.vaadin.flow.internal.ReflectTools.getPropertyName;
import static com.vaadin.flow.internal.ReflectTools.isGetter;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.HasElement;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.internal.ReflectTools;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayDeque;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Supplier;
import lombok.NonNull;
import lombok.val;

public class PathSelection<T> implements Selection<T> {

  private final String path;
  private final Class<T> type;
  private final UserInterface ui;
  private final HasElement component;

  public PathSelection(UserInterface ui, @NonNull String path, @NonNull Class<T> type) {
    this.ui = ui;
    this.type = type;
    this.path = path;
    this.component = null;
  }

  public PathSelection(UserInterface ui, HasElement component, @NonNull String path,
      @NonNull Class<T> type) {
    this.ui = ui;
    this.type = type;
    this.path = path;
    this.component = component;
  }


  static ArrayDeque<String> split(String path) {
    val result = new ArrayDeque<String>();
    val buffer = new StringBuilder(path.length());
    val limit = path.length();
    val chars = path.toCharArray();
    for (int i = 0; i < limit; i++) {
      char ch = chars[i];
      if (ch == ':' && i > 0) {
        result.add(buffer.toString());
        buffer.setLength(0);
      }
      buffer.append(ch);
    }
    result.add(buffer.toString());
    return result;
  }

  private static String normalize(String other) {
    if (other == null) {
      return null;
    }
    if (other.charAt(0) == ':') {
      return other;
    }
    return ":" + other;
  }

  @Override
  public Optional<T> select(Supplier<UI> supplier) {
    return Optional.ofNullable(supplier.get()).map(this::locate);
  }

  @SuppressWarnings("unchecked")
  private T locate(UI ui) {
    val stack = new ArrayDeque<Component>();
    val registry = this.ui.getExtensionRegistry();

    val component = Optional.ofNullable((Component) this.component)
        .or(ui.getElement()::getComponent);
    component.ifPresent(stack::push);
    val segments = split(path);
    while (!stack.isEmpty()) {
      val comp = stack.pop();
      for (var type = registry.typeOf(comp);
          !Objects.equals(type, Object.class);
          type = type.getSuperclass()) {
        val host = type.getAnnotation(Host.class);
        if (host != null && Objects.equals(normalize(host.value()), segments.peekFirst())) {
          segments.pop();
          if (segments.isEmpty()) {
            return (T) comp;
          }
          try {
            var result = searchFor(registry.typeOf(comp), comp, segments);
            if (result != null) {
              return (T) result;
            }
          } catch (Exception ex) {
            throw new IllegalStateException(ex);
          }
        }
      }
      comp.getChildren().forEach(stack::push);
    }
    return null;
  }

  @SuppressWarnings("unchecked")
  private <T> T searchFor(Class<?> type, Component instance, ArrayDeque<String> slot)
      throws IllegalAccessException, InvocationTargetException {
    while (!slot.isEmpty()) {
      val slotValue = slot.pollFirst();
      for (var c = type; !Objects.equals(c, Object.class); c = c.getSuperclass()) {

        for (val field : c.getDeclaredFields()) {
          val slotAnnotation = field.getAnnotation(Slot.class);
          if (slotAnnotation != null && Objects.equals(slotAnnotation.value(), slotValue)) {
            if (!field.trySetAccessible()) {
              throw new IllegalStateException(
                  String.format(
                      "Error: field '%s' on type '%s' for slot '%s' is not accessible!",
                      field, c, slotValue));
            }
            return (T) field.get(instance);
          }
        }

        for (val method : c.getDeclaredMethods()) {
          val slotAnnotation = method.getAnnotation(Slot.class);
          if (slotAnnotation != null && Objects.equals(slotAnnotation.value(), slotValue)) {
            final Method getter;
            if (isGetter(method)) {
              getter = method;
            } else {
              val propertyName = ReflectTools.getPropertyName(method);
              val f = c;
              getter =
                  ReflectTools.getGetterMethods(c)
                      .filter(m -> Objects.equals(propertyName, getPropertyName(m)))
                      .findFirst()
                      .orElseThrow(
                          () ->
                              new IllegalStateException(
                                  String.format(
                                      "Error: method '%s' on type '%s' for slot '%s' does not have a corresponding getter method!",
                                      method, f, slotValue)));
            }

            if (!getter.trySetAccessible()) {
              throw new IllegalStateException(
                  String.format(
                      "Error: method '%s' on type '%s' for slot '%s' is not accessible!",
                      method, c, slotValue));
            }
            return (T) getter.invoke(instance);
          }
        }
      }
    }
    return null;
  }
}
