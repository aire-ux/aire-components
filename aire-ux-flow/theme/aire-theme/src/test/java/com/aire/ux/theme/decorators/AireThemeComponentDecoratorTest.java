package com.aire.ux.theme.decorators;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import com.aire.ux.test.AireTest;
import com.aire.ux.test.Navigate;
import com.aire.ux.test.Routes;
import com.aire.ux.test.Select;
import com.aire.ux.test.ViewTest;
import com.aire.ux.test.spring.EnableSpring;
import com.aire.ux.theme.decorators.AireThemeComponentDecoratorTest.Cfg;
import com.aire.ux.theme.decorators.scenario1.MainView;
import com.aire.ux.theme.decorators.scenario1.TestButton;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.ContextConfiguration;

/** this needs to come first, unfortunately */
@EnableThemes
@AireTest
@EnableSpring
@ContextConfiguration(classes = Cfg.class)
@Routes(scanClassPackage = MainView.class)
@TestTheme(AireTestTheme.class)
public
class AireThemeComponentDecoratorTest {

  @ViewTest
  @Navigate("main")
  void ensureTestButtonIsDecorated(@Select("aire-button.test-theme") TestButton button) {
    assertNotNull(button);
  }

  @Configuration
  public static class Cfg {}
}
