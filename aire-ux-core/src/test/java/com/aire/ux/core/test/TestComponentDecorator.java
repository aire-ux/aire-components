package com.aire.ux.core.test;

import org.jetbrains.annotations.NotNull;
import com.vaadin.flow.component.HasElement;
import com.aire.ux.core.decorators.ComponentDecorator;

public class TestComponentDecorator implements ComponentDecorator {

  @Override
  public void decorate(@NotNull HasElement component) {
    component.getElement().setProperty("hello", "world");
  }
}
