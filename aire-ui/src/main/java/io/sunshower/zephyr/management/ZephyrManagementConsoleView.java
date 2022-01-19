package io.sunshower.zephyr.management;

import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.tabs.Tab;
import com.vaadin.flow.component.tabs.Tabs;
import com.vaadin.flow.component.template.Id;
import com.vaadin.flow.data.provider.ListDataProvider;
import com.vaadin.flow.router.Route;
import io.sunshower.zephyr.MainView;
import io.sunshower.zephyr.ui.components.Panel;
import io.sunshower.zephyr.ui.controls.NavigationBarButton;
import io.sunshower.zephyr.ui.layout.ApplicationLayout;
import io.sunshower.zephyr.ui.layout.ApplicationLayoutDecorator;
import io.zephyr.cli.Zephyr;
import io.zephyr.kernel.Coordinate;
import javax.inject.Inject;
import lombok.val;

@Route(value = "zephyr/management", layout = ApplicationLayout.class)
public class ZephyrManagementConsoleView extends Panel implements ApplicationLayoutDecorator {

  private final Zephyr zephyr;
  @Id("grid")
  private final Grid<Coordinate> modules;
  private NavigationBarButton homeButton;

  @Inject
  public ZephyrManagementConsoleView(final Zephyr zephyr) {
    this.zephyr = zephyr;
    this.modules = populateGrid();
    add(modules);
  }

  private Grid<Coordinate> populateGrid() {

    val moduleTab = new Tab("Modules");
    val topologyTab = new Tab("Topology");
    val tabs = new Tabs(moduleTab, topologyTab);
    add(tabs);

    val grid = new Grid<Coordinate>();
    grid.addColumn(Coordinate::getGroup).setHeader("Group");
    grid.addColumn(Coordinate::getName).setHeader("Name");
    grid.addColumn(Coordinate::getVersion).setHeader("Version");
    grid.setItems(new ListDataProvider<>(zephyr.getPluginCoordinates()));
    return grid;
  }

  @Override
  public void decorate(ApplicationLayout layout) {
    homeButton = new NavigationBarButton(MainView.class, new Image("images/icon.svg", "Home"));
    homeButton.setClassName("container-end");
    layout.getTop().add(homeButton);

    val homeButton = new NavigationBarButton(ZephyrManagementConsoleView.class,
        VaadinIcon.PLUG.create());

    layout.getNavigation().add(homeButton);
  }

  public void undecorate(ApplicationLayout layout) {
    if (homeButton != null) {
      layout.getTop().remove(homeButton);
    }
  }
}
