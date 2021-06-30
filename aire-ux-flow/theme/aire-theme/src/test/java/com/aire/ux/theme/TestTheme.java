package com.aire.ux.theme;

import com.aire.ux.theme.decorators.scenario1.TestButton;
import com.aire.ux.theme.decorators.scenario1.TestButtonDecorator;

public class TestTheme extends BaseTheme {

  public static String id = "test-theme";

  public TestTheme() {
    super(id, TestTheme.class.getClassLoader());
    register(TestButton.class, TestButtonDecorator.class);
    addResource(new JavascriptResource("resource.js", "test-theme/resource.js"));
    addResource(new StylesheetResource("test.css", "test-theme/styles/test.css"));
  }
}
