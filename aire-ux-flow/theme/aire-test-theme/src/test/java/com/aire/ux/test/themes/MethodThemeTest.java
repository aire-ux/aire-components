package com.aire.ux.test.themes;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import com.aire.ux.DefaultTheme;
import com.aire.ux.Theme;
import com.aire.ux.test.AireTest;
import com.aire.ux.test.Context;
import com.aire.ux.test.ViewTest;

@AireTest
@EnableThemes
public class MethodThemeTest {

  @ViewTest
  @DefaultTheme(TestTheme1.class)
  void ensureThemeIsInjectable(@Context Theme theme) {
    assertNotNull(theme);
  }

  @ViewTest
  @DefaultTheme(TestTheme2.class)
  void ensureThemeHasCorrectType2(@Context Theme theme) {
    assertEquals(TestTheme2.class, theme.getClass());
  }

  @ViewTest
  @DefaultTheme(TestTheme1.class)
  void ensureThemeHasCorrectType(@Context Theme theme) {
    assertEquals(TestTheme1.class, theme.getClass());
  }
}
