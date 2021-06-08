package com.aire.ux.test;

import com.aire.ux.plan.DefaultPlanContext;
import com.aire.ux.select.css.CssSelectorParser;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.dom.Element;
import io.sunshower.arcus.reflect.Reflect;
import io.sunshower.lambda.Option;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import lombok.val;
import org.jetbrains.annotations.NotNull;

public class DefaultTestContext implements TestContext {

  @Override
  @SuppressWarnings("unchecked")
  public <T> Optional<T> selectFirst(String selector, Class<T> type) {
    return selectComponents(selector).stream()
        .filter(t -> type.isAssignableFrom(t.getClass()))
        .map(t -> (T) t)
        .findFirst();
  }

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
  public List<Element> selectElements(Class<?>... types) {
    val elements = new ArrayDeque<Element>();
    val predicate = elementTypePredicate(types);
    elements.add(UI.getCurrent().getElement());
    val results = new ArrayList<Element>();
    while (!elements.isEmpty()) {
      val element = elements.remove();
      if (predicate.test(element)) {
        results.add(element);
      }
      for (int i = 0; i < element.getChildCount(); i++) {
        elements.add(element.getChild(i));
      }
    }
    return results;
  }

  @Override
  @SuppressWarnings("unchecked")
  public <T> List<T> select(Class<T> type) {
    val elements = new ArrayDeque<Element>();
    val predicate = elementTypePredicate(type);
    elements.add(UI.getCurrent().getElement());
    val results = new ArrayList<T>();
    while (!elements.isEmpty()) {
      val element = elements.remove();
      if (predicate.test(element)) {
        results.add(
            (T)
                element
                    .getComponent()
                    .orElseThrow(
                        () ->
                            new NoSuchElementException(
                                "Element '%s' matched, but did not reference a component of type '%s'"
                                    .formatted(element, type))));
      }
      for (int i = 0; i < element.getChildCount(); i++) {
        elements.add(element.getChild(i));
      }
    }
    return results;
  }

  @Override
  @SuppressWarnings("unchecked")
  public <T extends Component> Optional<T> selectFirst(Class<T> type) {
    val elements = new ArrayDeque<Element>();
    val predicate = elementTypePredicate(type);
    elements.add(UI.getCurrent().getElement());
    while (!elements.isEmpty()) {
      val element = elements.remove();
      if (predicate.test(element)) {
        return (Optional<T>) element.getComponent();
      }
      for (int i = 0; i < element.getChildCount(); i++) {
        elements.add(element.getChild(i));
      }
    }
    return Optional.empty();
  }

  @Override
  public List<?> selectComponents(String selector) {
    return new CssSelectorParser()
            .parse(selector)
            .plan(DefaultPlanContext.getInstance())
            .evaluate(UI.getCurrent().getElement(), new ComponentHierarchyNodeAdapter())
            .stream()
            .flatMap(t -> t.getComponent().stream())
            .collect(Collectors.toList());
  }

  @Override
  public void navigate(String route) {
    UI.getCurrent().navigate(route);
  }

  private Predicate<Element> elementTypePredicate(Class<?>... types) {
    if (types == null || types.length == 0) {
      return element -> true;
    }
    return element ->
        Option.from(element.getComponent())
            .flatMap(
                component -> {
                  val componentType = component.getClass();
                  for (val t : types) {
                    if (componentType.equals(t) || Reflect.isCompatible(componentType, t)) {
                      return Option.of(true);
                    }
                  }
                  return Option.none();
                })
            .isSome();
  }
}
