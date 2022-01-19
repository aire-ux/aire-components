package io.sunshower.zephyr.management;

import com.vaadin.flow.component.AttachEvent;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.router.ParentLayout;
import com.vaadin.flow.router.RoutePrefix;
import com.vaadin.flow.shared.Registration;
import io.sunshower.zephyr.MainView;
import io.sunshower.zephyr.ui.Home;
import io.sunshower.zephyr.ui.components.Panel;
import io.sunshower.zephyr.ui.controls.NavigationBarButton;
import io.sunshower.zephyr.ui.controls.NavigationBarButton.MatchMode;
import io.sunshower.zephyr.ui.layout.ApplicationLayout;
import io.sunshower.zephyr.ui.layout.ApplicationLayoutDecorator;
import io.zephyr.cli.Zephyr;
import java.util.List;
import javax.inject.Inject;
import lombok.val;

@RoutePrefix("zephyr")
@ParentLayout(ApplicationLayout.class)
public class ZephyrManagementConsoleView extends Panel implements ApplicationLayoutDecorator {

  private final Zephyr zephyr;
  private NavigationBarButton homeButton;

  @Inject
  public ZephyrManagementConsoleView(final Zephyr zephyr) {
    this.zephyr = zephyr;
  }


  @Override
  public Registration addAttachListener(ComponentEventListener<AttachEvent> listener) {
    return super.addAttachListener(listener);
  }

//  private Grid<Coordinate> populateGrid() {
//
//
//
////    val moduleTab = new Tab("Modules");
////    val topologyTab = new Tab("Topology");
////    val addTab = new Tab(VaadinIcon.PLUS.create());
////    val tabs = new Tabs(moduleTab, topologyTab, addTab);
////    add(tabs);
////
////    val grid = new Grid<Coordinate>();
////    grid.addThemeVariants(GridVariant.LUMO_NO_BORDER);
////    grid.addColumn(Coordinate::getGroup).setHeader("Group");
////    grid.addColumn(Coordinate::getName).setHeader("Name");
////    grid.addColumn(Coordinate::getVersion).setHeader("Version");
//////    grid.setItems(new ListDataProvider<>(zephyr.getPluginCoordinates()));
////    grid.setItems(getPluginCoordinates());
//////    grid.getStyle().set("position", "unset");
////
////    return grid;
//  }
//

  //  List<Coordinate> getPluginCoordinates() {
//    val result = new ArrayList<Coordinate>();
//    for (int i = 0; i < 1000; i++) {
//      val coordinate = new ModuleCoordinate("test-plugin" + i, "test",
//          new SemanticVersion("1.0.0"));
//
//      result.add(coordinate);
//    }
//    return result;
//  }
//
  @Override
  public void decorate(ApplicationLayout layout) {
    homeButton = new NavigationBarButton(MainView.class, new Image("images/icon.svg", "Home"));
    homeButton.setClassName("container-end");
    layout.getTop().add(homeButton);

    val homeButton = new NavigationBarButton(ModuleGrid.class,
        List.of("modules/list", "modules/topology"),
        MatchMode.Suffix,
        VaadinIcon.PLUG.create());

    val infoButton = new NavigationBarButton(Home.class, VaadinIcon.HOME.create());

    layout.getNavigation().add(homeButton);
    layout.getNavigation().add(infoButton);
  }

  public void undecorate(ApplicationLayout layout) {
    if (homeButton != null) {
      layout.getTop().remove(homeButton);
    }
  }
}
