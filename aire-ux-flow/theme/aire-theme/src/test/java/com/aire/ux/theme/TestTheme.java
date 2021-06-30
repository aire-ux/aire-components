package com.aire.ux.theme;

public class TestTheme extends BaseTheme {

  public static String id = "test-theme";

  public TestTheme() {
    super(id, TestTheme.class.getClassLoader());
    addResource(new JavascriptResource("test-theme/resource.js", "resource.js"));
  }
}
