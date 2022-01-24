package com.aire.ux.test.resolvers;

import com.aire.ux.test.ElementResolver;
import com.aire.ux.test.ElementResolverFactory;
import com.aire.ux.test.View;
import com.vaadin.flow.server.VaadinServlet;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Parameter;

public class ViewElementResolverFactory implements ElementResolverFactory {

  @Override
  public boolean appliesTo(AnnotatedElement element) {
    return element.isAnnotationPresent(View.class) && element instanceof Parameter;
  }

  @Override
  public ElementResolver create(AnnotatedElement element) {
    return new ViewElementResolver(element);
  }

  private static class ViewElementResolver implements ElementResolver {

    private final Parameter element;

    public ViewElementResolver(
        AnnotatedElement element) {
      this.element = (Parameter) element;
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> T resolve() {
      return (T) VaadinServlet.getCurrent().getService().getInstantiator()
          .getOrCreate(element.getType());
    }
  }
}
