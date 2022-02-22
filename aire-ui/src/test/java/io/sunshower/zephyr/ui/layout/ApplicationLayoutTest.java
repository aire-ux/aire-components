package io.sunshower.zephyr.ui.layout;

import static org.junit.jupiter.api.Assertions.assertFalse;

import com.aire.ux.test.Context;
import com.aire.ux.test.Routes;
import com.aire.ux.test.TestContext;
import com.aire.ux.test.ViewTest;
import io.sunshower.zephyr.AireUITest;
import io.sunshower.zephyr.ui.layout.scenario1.MainNavigationComponent;

@AireUITest
@Routes(scanClassPackage = MainNavigationComponent.class)
class ApplicationLayoutTest {

  @ViewTest
  void ensureMainNavigationIsNotSelectable(@Context TestContext $) {
    assertFalse($.selectFirst(MainNavigationComponent.class).isPresent());
  }

}