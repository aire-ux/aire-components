package com.aire.ux.test.themes;

import com.aire.ux.theme.BaseTheme;

public class TestTheme2 extends BaseTheme {

  public TestTheme2() {
    super("test-them-3", TestTheme1.class.getClassLoader());
  }
}
