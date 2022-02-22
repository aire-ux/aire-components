package com.aire.ux.test;

import com.aire.ux.ext.ExtensionRegistry;
import com.vaadin.flow.server.VaadinServlet;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Parameter;

public class ExtensionRegistryParameterResolver implements ElementResolverFactory {

  @Override
  public boolean appliesTo(AnnotatedElement element) {
    if (element.isAnnotationPresent(Context.class) && element instanceof Parameter) {
      return ExtensionRegistry.class.isAssignableFrom(((Parameter) element).getType());
    }
    return false;
  }

  @Override
  @SuppressWarnings("unchecked")
  public ElementResolver create(AnnotatedElement element) {
    return new ElementResolver() {
      @Override
      public <T> T resolve() {
        return (T) VaadinServlet.getCurrent().getService().getInstantiator()
            .getOrCreate(ExtensionRegistry.class);
      }
    };
  }


}
