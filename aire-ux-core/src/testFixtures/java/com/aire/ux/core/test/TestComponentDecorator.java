package com.aire.ux.core.test;

import com.aire.ux.ComponentDecorator;
import com.vaadin.flow.component.HasElement;
import javax.annotation.Nonnull;

public class TestComponentDecorator implements ComponentDecorator {

  @Override
  public void decorate(@Nonnull HasElement component) {
    component.getElement().setProperty("hello", "world");
  }
}
