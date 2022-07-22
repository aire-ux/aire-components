package com.aire.ux.theme;

import static org.junit.jupiter.api.Assertions.assertTrue;

import com.aire.ux.test.AireTest;
import com.aire.ux.test.Context;
import com.aire.ux.test.Navigate;
import com.aire.ux.test.RouteLocation;
import com.aire.ux.test.TestContext;
import com.aire.ux.test.ViewTest;
import com.aire.ux.test.spring.EnableSpring;
import com.aire.ux.theme.ThemeTest.Cfg;
import com.aire.ux.theme.decorators.AireTestTheme;
import com.aire.ux.theme.decorators.EnableThemes;
import com.aire.ux.theme.decorators.TestTheme;
import com.aire.ux.theme.decorators.scenario1.MainView;
import com.aire.ux.theme.decorators.scenario1.TestButton;
import lombok.val;
import org.junit.jupiter.api.Disabled;
import org.springframework.test.context.ContextConfiguration;

@EnableThemes
@AireTest
@Disabled
@EnableSpring
@ContextConfiguration(classes = Cfg.class)
@RouteLocation(scanClassPackage = MainView.class)
@TestTheme(AireTestTheme.class)
public class ThemeTest {

  @ViewTest
  @Navigate("main")
  void ensureThemeDecoratesButton(@Context TestContext context) {
    val result = context.selectFirst("aire-button.test-theme", TestButton.class);
    assertTrue(result.isPresent());
  }

  @ContextConfiguration
  static class Cfg {}
}
