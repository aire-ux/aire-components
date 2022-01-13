package com.aire.ux.annotations;

import com.aire.ux.Host;
import com.aire.ux.core.decorators.ComponentDecorator;
import com.vaadin.flow.component.HasElement;
import javax.annotation.Nonnull;
import lombok.val;

public class HostDecorator implements ComponentDecorator {

  public HostDecorator() {
    System.out.println("HOSt");
  }
  @Override
  public void decorate(@Nonnull HasElement component) {
    val hostAnnotation = component.getClass().getAnnotation(Host.class);
    if(hostAnnotation != null) {

    }

  }
}
