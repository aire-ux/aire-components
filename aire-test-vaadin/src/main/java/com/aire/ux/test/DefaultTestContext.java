package com.aire.ux.test;

import static java.lang.String.format;

import com.aire.ux.test.Context.Mode;
import com.aire.ux.test.vaadin.Frames;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.di.Instantiator;
import com.vaadin.flow.dom.Element;
import io.sunshower.arcus.reflect.Reflect;
import io.sunshower.arcus.selectors.css.CssSelectorParser;
import io.sunshower.arcus.selectors.plan.DefaultPlanContext;
import io.sunshower.lambda.Option;
import java.io.Flushable;
import java.io.IOException;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import lombok.extern.java.Log;
import lombok.val;
import org.jetbrains.annotations.NotNull;

@Log
public class DefaultTestContext implements TestContext {

  private final Supplier<Element> supplier;

  public DefaultTestContext(Supplier<Element> supplier) {
    this.supplier = Objects.requireNonNull(supplier);
  }

  public DefaultTestContext() {
    this(() -> UI.getCurrent().getElement());
  }

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

    elements.add(supplier.get());
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
    elements.add(supplier.get());
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
    elements.add(supplier.get());
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
                                format(
                                    "Element '%s' matched, but did not reference a component of type '%s'",
                                    element, type))));
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
    elements.add(supplier.get());
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
            .evaluate(
                supplier.get(),
                Optional.ofNullable(Frames.getCurrentNodeAdapter())
                    .orElse(new ComponentHierarchyNodeAdapter()))
            .stream()
            .flatMap(t -> t.getComponent().stream())
            .collect(Collectors.toList());
  }

  @Override
  public void navigate(String route) {
    UI.getCurrent().navigate(route);
  }

  @Override
  public void navigate(Class<? extends Component> route) {
    UI.getCurrent().navigate(route);
  }

  @Override
  public <T> T resolve(Class<T> contextClass, Mode mode) {
    return Frames.resolveCurrentFrame().resolveContextVariable(contextClass, mode);
  }

  @Override
  public <T extends Component> TestContext downTo(Class<T> contextClass) {
    return new DefaultTestContext(() -> selectFirst(contextClass).get().getElement());
  }

  public <T extends Component> TestContext downTo(T type) {
    return new DefaultTestContext(type::getElement);
  }

  @Override
  public void flush(boolean force) {
    Optional.ofNullable(UI.getCurrent())
        .map(
            ui -> {
              ui.push();
              return ui;
            })
        .map(Instantiator::get)
        .ifPresent(
            instantiator -> {
              if (instantiator instanceof Flushable) {
                try {
                  ((Flushable) instantiator).flush();
                } catch (IOException ex) {
                  log.warning("Failed to flush instantiator");
                }
              }
            });
    if (force) {
      Frames.reload();
    }
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
