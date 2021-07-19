package com.aire.ux.theme.themescenario;

import com.aire.ux.theme.BaseTheme;
import com.aire.ux.theme.Decorator;

public class ScenarioTheme extends BaseTheme {

  public ScenarioTheme() {
    super("scenario-theme", Thread.currentThread().getContextClassLoader());
    register(TestButton.class, TestButtonDecorator.class);
  }

  public static class TestButtonDecorator implements Decorator<TestButton> {

    @Override
    public Class<TestButton> getTarget() {
      return TestButton.class;
    }

    @Override
    public void decorate(TestButton value) {
      value.addClassNames("sup");
    }
  }
}
