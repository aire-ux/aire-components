package com.aire.ux.test.resolvers;

import com.aire.ux.test.Context;
import com.aire.ux.test.ElementResolver;
import com.aire.ux.test.ElementResolverFactory;
import com.vaadin.flow.di.Instantiator;
import com.vaadin.flow.server.VaadinServlet;
import io.sunshower.arcus.reflect.Reflect;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Parameter;

public class InstantiatorElementResolverFactory implements ElementResolverFactory {

  @Override
  public boolean appliesTo(AnnotatedElement element) {
    if (element.isAnnotationPresent(Context.class) && element instanceof Parameter) {
      return Reflect.isCompatible(Instantiator.class, ((Parameter) element).getType());
    }
    return false;
  }

  @Override
  public ElementResolver create(AnnotatedElement element) {
    return new InstantiatorResolvingElementResolver();
  }

  private static class InstantiatorResolvingElementResolver implements ElementResolver {

    @Override
    @SuppressWarnings("unchecked")
    public <T> T resolve() {
      return (T) VaadinServlet.getCurrent().getService().getInstantiator();
    }
  }
}
