package com.aire.ux.core.test;

import com.aire.ux.core.decorators.ComponentDecorator;
import com.vaadin.flow.component.HasElement;
import org.jetbrains.annotations.NotNull;

public class TestComponentDecorator implements ComponentDecorator {

  @Override
  public void decorate(@NotNull HasElement component) {
    component.getElement().setProperty("hello", "world");
  }
}
