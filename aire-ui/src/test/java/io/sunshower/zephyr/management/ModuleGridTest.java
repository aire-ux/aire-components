package io.sunshower.zephyr.management;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import com.aire.ux.test.Context;
import com.aire.ux.test.Navigate;
import com.aire.ux.test.Routes;
import com.aire.ux.test.Select;
import com.aire.ux.test.TestContext;
import com.aire.ux.test.View;
import com.aire.ux.test.ViewTest;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.data.provider.Query;
import com.vaadin.flow.di.Instantiator;
import com.vaadin.flow.router.RouterLink;
import io.sunshower.zephyr.AireUITest;
import lombok.val;

@AireUITest
@Navigate("zephyr/management/modules/list")
@Routes(scanClassPackage = ModuleGrid.class)
class ModuleGridTest {

  @ViewTest
  void ensureModuleGridIsInjectable(
      @Select("a[href='zephyr/management/modules/list']") RouterLink tag,
      @Context TestContext context) {
    assertNotNull(tag, "anchor tag must exist");
  }

  @ViewTest
  void ensureTabPanelIsInjectable(@Select PluginTabView panel) {
    assertNotNull(panel, "Panel must not be null");
  }

  @ViewTest
  void ensureModuleGridHasCorrectLayout(@Context TestContext context,
      @Context Instantiator instantiator) {
    val grid = instantiator.createComponent(ModuleGrid.class);
    val $ = context.downTo(grid);
    val columns = $.select(Grid.Column.class);
    assertEquals(columns.size(), 5);
  }

  @ViewTest
  @SuppressWarnings({"unchecked", "rawtypes"})
  void ensureModuleGridContainsDefaultPlugin(@Context TestContext context,
      @Context Instantiator instantiator) {
    val grid = instantiator.createComponent(ModuleGrid.class);
    val $ = context.downTo(grid);
    val g = $.selectFirst(Grid.class).get();
    assertEquals(g.getDataProvider().size(new Query()), 1);
  }

  @ViewTest
  void ensureModuleGridIsInjectable(@View ModuleGrid grid) {
    assertNotNull(grid);
  }
}