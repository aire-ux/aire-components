package io.sunshower.zephyr.management;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.template.Id;
import com.vaadin.flow.data.provider.ListDataProvider;
import com.vaadin.flow.router.Route;
import io.sunshower.zephyr.ui.layout.ApplicationLayout;
import io.zephyr.cli.Zephyr;
import io.zephyr.kernel.Coordinate;
import javax.inject.Inject;
import lombok.val;

@Route("zephyr/management")
public class ZephyrManagementConsoleView extends ApplicationLayout {

  private final Zephyr zephyr;
  @Id("grid")
  private final Grid<Coordinate> modules;

  @Inject
  public ZephyrManagementConsoleView(final Zephyr zephyr) {
    this.zephyr = zephyr;
    this.modules = populateGrid();
    add(modules);
    add(new Button("Hello"));
  }

  private Grid<Coordinate> populateGrid() {
    val grid = new Grid<Coordinate>();
    grid.addColumn(Coordinate::getGroup).setHeader("Group");
    grid.addColumn(Coordinate::getName).setHeader("Name");
    grid.addColumn(Coordinate::getVersion).setHeader("Version");
    grid.setItems(new ListDataProvider<>(zephyr.getPluginCoordinates()));
    return grid;
  }
}
