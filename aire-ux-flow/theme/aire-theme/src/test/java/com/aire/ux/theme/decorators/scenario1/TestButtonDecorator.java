package com.aire.ux.theme.decorators.scenario1;

import com.aire.ux.theme.Decorator;

public class TestButtonDecorator implements Decorator<TestButton> {

  @Override
  public Class<TestButton> getTarget() {
    return TestButton.class;
  }

  @Override
  public void decorate(TestButton value) {
    value.setClassName("test-theme");
  }
}
