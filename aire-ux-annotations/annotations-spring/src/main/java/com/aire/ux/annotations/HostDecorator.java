package com.aire.ux.annotations;

import com.aire.ux.Host;
import com.aire.ux.core.decorators.ComponentDecorator;
import com.vaadin.flow.component.HasElement;
import java.lang.reflect.InvocationTargetException;
import javax.annotation.Nonnull;
import lombok.val;

public class HostDecorator implements ComponentDecorator {

  public static final String METHOD_NAME = "setId";

  public HostDecorator() {

  }

  @Override
  public void decorate(@Nonnull HasElement component) {
    val type = component.getClass();
    val annotation = type.getAnnotation(Host.class);
    if (annotation != null) {
      try {
        val method = type.getMethod(METHOD_NAME, String.class);
        method.invoke(component, annotation.value());
      } catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException e) {
        throw new IllegalStateException("Error: " + e, e);
      }
    }
  }
}
