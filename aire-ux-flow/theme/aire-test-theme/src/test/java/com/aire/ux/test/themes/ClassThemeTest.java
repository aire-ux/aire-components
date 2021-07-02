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
@DefaultTheme(TestTheme1.class)
public class ClassThemeTest {

  @ViewTest
  void ensureThemeIsInjectable(@Context Theme theme) {
    assertNotNull(theme);
  }

  @ViewTest
  void ensureThemeHasCorrectType(@Context Theme theme) {
    assertEquals(TestTheme1.class, theme.getClass());
  }
}
