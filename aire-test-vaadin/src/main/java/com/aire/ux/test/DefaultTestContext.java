package com.aire.ux.test;


import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.dom.Element;
import io.sunshower.arcus.reflect.Reflect;
import io.sunshower.lambda.Option;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.function.Predicate;
import lombok.val;
import org.jetbrains.annotations.NotNull;

public class DefaultTestContext implements TestContext {

  @NotNull
  @Override
  public List<?> selectComponents(Class<?>... types) {
    val elements = new ArrayDeque<Element>();
    val result = new ArrayList<>();
    val predicate = elementTypePredicate(types);

    elements.add(UI.getCurrent().getElement());
    while (!elements.isEmpty()) {
      val element = elements.remove();
      if (predicate.test(element)) {
        element.getComponent().ifPresent(result::add);
      }
      for (int i = 0; i < element.getChildCount(); i++) {
        elements.add(element.getChild(i));
      }
    }
    return result;
  }

  @NotNull
  @Override
  public List<?> selectElements(Class<?>... types) {
    return null;
  }


  @Override
  public <T> List<T> select(Class<T> type) {
    return null;
  }

  @Override
  @SuppressWarnings("unchecked")
  public <T extends Component> T selectFirst(Class<T> type) {
    val elements = new ArrayDeque<Element>();
    val predicate = elementTypePredicate(type);

    elements.add(UI.getCurrent().getElement());
    while (!elements.isEmpty()) {
      val element = elements.remove();
      if (predicate.test(element)) {
        return (T) element.getComponent().orElseThrow(() -> new NoSuchElementException(
            "Element '%s' matched, but did not reference a component of type '%s'"
                .formatted(element, type)));
      }
      for (int i = 0; i < element.getChildCount(); i++) {
        elements.add(element.getChild(i));
      }
    }
    throw new NoSuchElementException("No component of type '%s' found in the current page");
  }

  @Override
  public List<?> selectComponents(String selector) {
    return null;
  }


  private Predicate<Element> elementTypePredicate(Class<?>... types) {
    if (types == null || types.length == 0) {
      return element -> true;
    }
    return element -> Option.from(element.getComponent()).flatMap(component -> {
      val componentType = component.getClass();
      for (val t : types) {
        if (Reflect.isCompatible(componentType, t)) {
          return Option.of(true);
        }
      }
      return Option.none();
    }).isSome();
  }
}
