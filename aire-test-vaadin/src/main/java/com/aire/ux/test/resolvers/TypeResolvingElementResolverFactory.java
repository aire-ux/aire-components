package com.aire.ux.test.resolvers;

import com.aire.ux.test.ElementResolver;
import com.aire.ux.test.ElementResolverFactory;
import com.aire.ux.test.Select;
import com.aire.ux.test.Utilities;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.dom.Element;
import io.sunshower.arcus.reflect.Reflect;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Parameter;
import java.lang.reflect.Type;
import java.util.ArrayDeque;
import java.util.Collection;
import java.util.NoSuchElementException;
import lombok.val;

public class TypeResolvingElementResolverFactory implements ElementResolverFactory {

  @Override
  public boolean appliesTo(AnnotatedElement element) {
    if (!(element instanceof Parameter)) {
      return false;
    }
    val selector = element.getAnnotation(Select.class);
    if (selector == null) {
      return false;
    }
    return Utilities.isDefault(selector);
  }

  @Override
  public ElementResolver create(AnnotatedElement element) {
    val parameter = (Parameter) element;
    val type = parameter.getType();
    if (Reflect.isCompatible(Collection.class, type)) {
      val optTypeParams = Reflect.getTypeParametersOfParameter(parameter);
      if (optTypeParams.isNone()) {
        throw new IllegalArgumentException(
            "Error: cannot resolve type-parameters of raw type (did you forget to specify one, e.g. List<String> instead of List?)");
      }
      return new CollectionTypeResolvingElementResolver(type, optTypeParams.get()[0]);
    } else {
      return new TypeResolvingElementResolver(type);
    }
  }


  private static class TypeResolvingElementResolver implements ElementResolver {

    final Class<?> type;
    public TypeResolvingElementResolver(
        Type type) {
      this.type = (Class<?>) type;
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> T resolve() {
      val result = new ArrayDeque<Element>();
      val init = UI.getCurrent().getElement();
      result.add(init);
      while(!result.isEmpty()) {
        val next = result.poll();
        if(next != null) {
          val component = next.getComponent();
          if(component.isPresent()) {
            val comp = component.get();
            if(type.isAssignableFrom(comp.getClass())) {
              return (T) comp;
            }
          }
        }
        val children = next.getChildren();
        if(children != null) {
          val citer = children.iterator();
          while (citer.hasNext()) {
            val child = citer.next();
            result.add(child);
          }
        }
      }
      throw new NoSuchElementException("No element of type: " + type + " found in the hierarchy");
    }
  }

  private class CollectionTypeResolvingElementResolver implements ElementResolver {

    public CollectionTypeResolvingElementResolver(
        Class<?> type, Type optTypeParam) {
    }

    @Override
    public <T> T resolve() {
      return null;
    }
  }
}
