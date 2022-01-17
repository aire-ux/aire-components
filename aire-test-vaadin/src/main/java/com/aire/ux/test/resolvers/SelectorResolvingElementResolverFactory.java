package com.aire.ux.test.resolvers;

import com.aire.ux.plan.DefaultPlanContext;
import com.aire.ux.select.css.CssSelectorParser;
import com.aire.ux.test.ComponentHierarchyNodeAdapter;
import com.aire.ux.test.ElementResolver;
import com.aire.ux.test.ElementResolverFactory;
import com.aire.ux.test.Select;
import com.aire.ux.test.Utilities;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.dom.Element;
import io.sunshower.arcus.reflect.Reflect;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Parameter;
import java.lang.reflect.ParameterizedType;
import java.util.Collection;
import java.util.stream.Collectors;
import lombok.val;

public class SelectorResolvingElementResolverFactory implements ElementResolverFactory {

  @Override
  public boolean appliesTo(AnnotatedElement element) {
    if (!(element instanceof Parameter)) {
      return false;
    }
    val selector = element.getAnnotation(Select.class);
    if (selector == null) {
      return false;
    }
    return !Utilities.isDefault(selector);
  }

  @Override
  @SuppressWarnings("unchecked")
  public ElementResolver create(AnnotatedElement element) {
    val param = (Parameter) element;
    val selector = element.getAnnotation(Select.class);
    if (Reflect.isCompatible(Collection.class, param.getType())) {
      val collectionType =
          (Class<? extends Collection<?>>) Utilities.resolveCollectionType(param.getType());
      val type = Reflect.getTypeParametersOfParameter(param);

      if (type.isNone()) {
        return new CollectionElementSelectorResolver(
            collectionType,
            Utilities.firstNonDefault(selector.selector(), selector.value()),
            true);
      }

      val actualType = type.get()[0];
      if (actualType instanceof ParameterizedType) {
        return new CollectionElementSelectorResolver(
            collectionType,
            Utilities.firstNonDefault(selector.selector(), selector.value()),
            Element.class.isAssignableFrom(
                (Class<?>) ((ParameterizedType) actualType).getRawType()));
      }
      boolean isElement = Element.class.isAssignableFrom((Class<?>) type.get()[0]);
      return new CollectionElementSelectorResolver(
          collectionType,
          Utilities.firstNonDefault(selector.selector(), selector.value()),
          isElement);
    } else {
      return new SingleElementSelectorResolver(
          Element.class.isAssignableFrom(param.getType()),
          Utilities.firstNonDefault(selector.selector(), selector.value()));
    }
  }

  private static final class CollectionElementSelectorResolver implements ElementResolver {

    private final String selector;
    private final boolean isElement;
    private final CssSelectorParser parser;
    private final Class<? extends Collection<?>> collection;

    public CollectionElementSelectorResolver(
        Class<? extends Collection<?>> collection, String selector, boolean isElement) {
      this.selector = selector;
      this.isElement = isElement;
      this.collection = collection;
      this.parser = new CssSelectorParser();
    }

    @Override
    @SuppressWarnings({"unchecked", "rawtypes"})
    public <T> T resolve() {
      val result =
          parser
              .parse(selector)
              .plan(DefaultPlanContext.getInstance())
              .evaluate(UI.getCurrent().getElement(), new ComponentHierarchyNodeAdapter());
      if (isElement) {
        val collect = Reflect.instantiate(collection);
        collect.addAll((Collection) result.results());
        return (T) collect;
      } else {
        return (T)
            result.results().stream()
                .flatMap(t -> t.getComponent().stream())
                .collect(
                    Collectors.toCollection(() -> (Collection) Reflect.instantiate(collection)));
      }
    }
  }

  private static final class SingleElementSelectorResolver implements ElementResolver {

    private final String selector;
    private final boolean element;
    private final CssSelectorParser parser;

    public SingleElementSelectorResolver(boolean element, String selector) {
      this.element = element;
      this.selector = selector;
      this.parser = new CssSelectorParser();
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> T resolve() {
      val result =
          parser
              .parse(selector)
              .plan(DefaultPlanContext.getInstance())
              .evaluate(UI.getCurrent().getElement(), new ComponentHierarchyNodeAdapter());
      if (result.size() > 0) {
        if (element) {
          return (T) result.results().iterator().next();
        } else {
          val element = (Element) result.results().iterator().next();
          return (T) element.getComponent().orElseThrow();
        }
      }
      return null;
    }
  }
}
