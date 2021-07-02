package com.aire.ux.theme;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import com.aire.ux.test.AireTest;
import com.aire.ux.test.Navigate;
import com.aire.ux.test.Routes;
import com.aire.ux.test.Select;
import com.aire.ux.test.ViewTest;
import com.aire.ux.test.spring.EnableSpring;
import com.aire.ux.theme.ThemeTest.Cfg;
import com.aire.ux.theme.themescenario.ScenarioView;
import com.aire.ux.theme.themescenario.TestButton;
import org.springframework.test.context.ContextConfiguration;

@AireTest
@EnableSpring
@ContextConfiguration(classes = Cfg.class)
@Routes(scanClassPackage = ScenarioView.class)
class AireThemeInstantiatorTest {

  @ViewTest
  @Navigate("scenario")
  void ensureDefaultThemeIsApplied(@Select(".sup") TestButton button) {
    assertNotNull(button);
  }

}