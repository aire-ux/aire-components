package io.sunshower.zephyr.ui.layout;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.aire.ux.test.Context;
import com.aire.ux.test.Navigate;
import com.aire.ux.test.RegisterExtension;
import com.aire.ux.test.Routes;
import com.aire.ux.test.Select;
import com.aire.ux.test.TestContext;
import com.aire.ux.test.ViewTest;
import com.vaadin.flow.component.button.Button;
import io.sunshower.zephyr.AireUITest;
import io.sunshower.zephyr.MainView;
import io.sunshower.zephyr.ui.layout.scenario1.MainNavigationComponent;

@AireUITest
@RegisterExtension(MainNavigationComponent.class)
@Routes(scanClassPackage = MainView.class)
@Navigate
class ApplicationLayoutTest {

  @ViewTest
  void ensureButtonIsInjectable(
      @Select("vaadin-button[text=Sup]") Button button, @Context TestContext $) {
    assertNotNull(button);
    assertTrue($.select(MainNavigationComponent.class).isEmpty());
    button.click();
    assertFalse($.select(MainNavigationComponent.class).isEmpty());
  }

  @ViewTest
  void ensureMainNavigationIsNotSelectable(@Context TestContext $) {
    assertFalse($.selectFirst(MainNavigationComponent.class).isPresent());
  }
}
