package io.sunshower.zephyr.management;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.aire.ux.test.Context;
import com.aire.ux.test.Navigate;
import com.aire.ux.test.Routes;
import com.aire.ux.test.Select;
import com.aire.ux.test.TestContext;
import com.aire.ux.test.ViewTest;
import io.sunshower.zephyr.AireUITest;
import io.sunshower.zephyr.ui.components.TabPanel;
import lombok.val;

@AireUITest
@Routes(scanClassPackage = ModuleGrid.class)
class PluginTabViewTest {

  @ViewTest
  @Navigate("zephyr/management/modules/list")
  void ensurePluginTabViewIsInjectable(@Context TestContext $) {
    val view = $.selectFirst("aire-tab-panel", TabPanel.class);
    assertTrue(view.isPresent());
  }

  @ViewTest
  @Navigate("zephyr/management/modules/list")
  void ensurePluginTabViewIsSelectableByPath(
      @Select(mode = "path", selector = ":main:module-management") PluginTabView view) {
    assertNotNull(view);
  }

}