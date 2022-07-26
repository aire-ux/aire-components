package com.aire.ux.core.decorators;

import com.aire.ux.DomAware;
import com.aire.ux.Element;
import com.aire.ux.core.adapters.ComponentHierarchyNodeAdapter;
import com.aire.ux.core.decorators.ComponentDecorator;
import com.vaadin.flow.component.HasElement;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.internal.JavaScriptBootstrapUI;
import io.sunshower.arcus.selectors.css.CssSelectorParser;
import io.sunshower.arcus.selectors.plan.DefaultPlanContext;
import io.sunshower.arcus.selectors.test.NodeAdapter;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;
import lombok.NonNull;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.springframework.util.ReflectionUtils;

@Slf4j
@SuppressWarnings("PMD")
public abstract class DomAwareComponentDecorator implements ComponentDecorator {

  static final CssSelectorParser parser = new CssSelectorParser();

  static final NodeAdapter<com.vaadin.flow.dom.Element> adapter =
      new ComponentHierarchyNodeAdapter();

  static final Field field = getField();

  @SneakyThrows
  static final Field getField() {
    val field = JavaScriptBootstrapUI.class.getDeclaredField("wrapperElement");
    if (!field.trySetAccessible()) {
      throw new IllegalStateException("No such field");
    }
    return field;
  }

  @SneakyThrows
  @Override
  public void onComponentEntered(@NonNull HasElement component) {
    if (component == null) {
      return;
    }
    val ui = UI.getCurrent();
    if (ui == null) {
      return;
    }
    val type = getTargetClass(component);
    val annotationPresent = type.isAnnotationPresent(DomAware.class);
    if (!annotationPresent) {
      return;
    }
    val wrapperElement = (com.vaadin.flow.dom.Element) field.get(ui);
    ReflectionUtils.doWithMethods(
        type,
        method -> {
          if (method.trySetAccessible()) {
            val annotation = method.getAnnotation(Element.class);
            val elements =
                parser
                    .parse(annotation.value())
                    .plan(DefaultPlanContext.getInstance())
                    .evaluate(wrapperElement, adapter);
            val arg = method.getParameterTypes()[0];
            for (val element : elements) {
              element
                  .getComponent()
                  .ifPresent(
                      c -> {
                        if (arg.isAssignableFrom(c.getClass())) {
                          try {
                            method.invoke(component, c);
                          } catch (InvocationTargetException | IllegalAccessException e) {
                            log.warn("Failed to set target");
                          }
                        }
                      });
              break;
            }
          }
        },
        method -> {
          val modifiers = method.getModifiers();
          return (Modifier.isPublic(modifiers) || Modifier.isProtected(modifiers))
              && method.isAnnotationPresent(Element.class);
        });
  }

  protected abstract <T> Class<T> getTargetClass(Object o);


  @Override
  public void onComponentExited(@NonNull HasElement component) {}
}
