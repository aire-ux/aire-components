package com.aire.ux.theme.decorators;

import com.aire.ux.theme.BaseTheme;
import com.aire.ux.theme.decorators.scenario1.TestButton;
import com.aire.ux.theme.decorators.scenario1.TestButtonDecorator;

public class AireTestTheme extends BaseTheme {

  public AireTestTheme() {
    super("aire-test-theme", Thread.currentThread().getContextClassLoader());
    register(TestButton.class, TestButtonDecorator.class);
  }
}
