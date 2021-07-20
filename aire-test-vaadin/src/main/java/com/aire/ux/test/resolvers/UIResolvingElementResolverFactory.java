package com.aire.ux.test.resolvers;

import com.aire.ux.test.Context;
import com.aire.ux.test.ElementResolver;
import com.aire.ux.test.ElementResolverFactory;
import com.aire.ux.test.vaadin.Frames;
import com.vaadin.flow.component.UI;
import io.sunshower.arcus.reflect.Reflect;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Parameter;

public class UIResolvingElementResolverFactory implements ElementResolverFactory {

  @Override
  public boolean appliesTo(AnnotatedElement element) {
    if (element.isAnnotationPresent(Context.class) && element instanceof Parameter) {
      return Reflect.isCompatible(UI.class, ((Parameter) element).getType());
    }
    return false;
  }

  @Override
  public ElementResolver create(AnnotatedElement element) {
    return new ElementResolver() {
      @Override
      @SuppressWarnings("unchecked")
      public <T> T resolve() {
        return (T) Frames.resolveCurrentFrame().get(UI.class);
      }
    };
  }
}
