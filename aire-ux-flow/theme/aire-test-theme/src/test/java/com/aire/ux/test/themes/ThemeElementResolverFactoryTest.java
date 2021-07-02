package com.aire.ux.test.themes;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import com.aire.ux.Theme;
import com.aire.ux.test.AireTest;
import com.aire.ux.test.Context;
import com.aire.ux.test.ViewTest;

@AireTest
class ThemeElementResolverFactoryTest {

  @ViewTest
  void ensureThemeIsInjectable(@Context Theme theme) {
    assertNotNull(theme);
  }
}
