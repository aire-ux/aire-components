package com.aire.ux.theme;

public class TestTheme extends BaseTheme {

  public static String id = "test-theme";

  public TestTheme() {
    super(id, TestTheme.class.getClassLoader());
    addResource(new JavascriptResource("resource.js", "test-theme/resource.js"));
    addResource(new StylesheetResource("test.css", "test-theme/styles/test.css"));
  }
}
