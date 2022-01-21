package io.sunshower.zephyr.management;

import com.vaadin.flow.component.AttachEvent;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.menubar.MenuBar;
import com.vaadin.flow.component.menubar.MenuBarVariant;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.Notification.Position;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.provider.ListDataProvider;
import com.vaadin.flow.router.Route;
import io.sunshower.zephyr.ui.components.Drawer;
import io.sunshower.zephyr.ui.components.Drawer.Direction;
import io.sunshower.zephyr.ui.components.Overlays;
import io.sunshower.zephyr.ui.components.Panel;
import io.sunshower.zephyr.ui.controls.DrawerNavigationBarButton;
import io.sunshower.zephyr.ui.layout.Layouts;
import io.sunshower.zephyr.ui.navigation.NavigationBar;
import io.zephyr.cli.Zephyr;
import io.zephyr.kernel.Coordinate;
import javax.inject.Inject;
import lombok.NonNull;
import lombok.val;

@Route(value = "modules/list", layout = PluginTabView.class)
public class ModuleGrid extends VerticalLayout {

  private final Zephyr zephyr;
  private final Drawer drawer;
  private final Grid<Coordinate> grid;
  private final NavigationBar navigationBar;
  private final DrawerNavigationBarButton moduleOpenAction;

  @Inject
  public ModuleGrid(@NonNull Zephyr zephyr) {
    this.zephyr = zephyr;
    this.setHeight("100%");
    add(createMenubar());
    add(grid = populateGrid());
    this.drawer = createDrawer();
    this.navigationBar = createNavigationBar();
    this.moduleOpenAction = createModuleOpenAction();
  }

  private DrawerNavigationBarButton createModuleOpenAction() {
    var button = new DrawerNavigationBarButton(
        VaadinIcon.INFO.create(),
        "Module Info",
        drawer,
        () -> new ModuleInfoPanel(zephyr, this::getSelectedCoordinate)
    );
    navigationBar.add(button);
    return button;
  }

  protected Coordinate getSelectedCoordinate() {
    val items = grid.getSelectedItems();
    if(items.isEmpty()) {
      return null;
    }
    return items.iterator().next();
  }




  private Drawer createDrawer() {
    return new Drawer(Direction.VerticalRight);
  }

  private NavigationBar createNavigationBar() {
    val navigationBar = new NavigationBar(NavigationBar.Direction.Vertical);
    navigationBar.setDrawer(drawer);
    return navigationBar;
  }

  private void addNavigation(Panel panel) {
    panel.setNavigationBar(navigationBar);
  }

  private Component createMenubar() {
    val result = new MenuBar();
    result.addThemeVariants(MenuBarVariant.LUMO_SMALL, MenuBarVariant.LUMO_ICON,
        MenuBarVariant.LUMO_TERTIARY_INLINE);
    val textField = new TextField();
    textField.setPlaceholder("Search");
    textField.setClearButtonVisible(true);
    textField.setPrefixComponent(VaadinIcon.SEARCH.create());
    result.addItem(textField);
    addButtonsToMenubar(result);
    return result;
  }


  private void notifySuccess() {
    val notification = new Notification("Successfully uploaded modules!");
    notification.addThemeVariants(NotificationVariant.LUMO_SUCCESS);
    notification.setDuration(5000);
    notification.setPosition(Position.TOP_STRETCH);
    notification.open();
  }


  private Grid<Coordinate> populateGrid() {
    val grid = new Grid<Coordinate>();
    grid.addThemeVariants(GridVariant.LUMO_NO_BORDER);
    grid.addItemClickListener(event -> {
      if(drawer.isClosed()) {
        drawer.open();
      }
      drawer.setContent(new ModuleInfoPanel(zephyr, () -> event.getItem()));
    });
    grid.addColumn(Coordinate::getGroup).setHeader("Group");
    grid.addColumn(Coordinate::getName).setHeader("Name");
    grid.addColumn(Coordinate::getVersion).setHeader("Version");
    grid.setItems(new ListDataProvider<>(zephyr.getPluginCoordinates()));
    return grid;
  }


  protected void onAttach(AttachEvent event) {
    Layouts.locateFirst(event, Panel.class).ifPresent(this::addNavigation);
  }

  private void addButtonsToMenubar(MenuBar result) {
    val button = new Button("Add Modules", VaadinIcon.PLUS.create());
    button.addClickListener(clickEvent -> {
      val overlay = Overlays.open(this, UploadPluginOverlay.class);
      overlay.addOverlayClosedEventListener(closed -> {
        if (!closed.isCancelled()) {
          grid.setItems(new ListDataProvider<>(zephyr.getPluginCoordinates()));
          notifySuccess();
        }
      });
    });
    result.addItem(button);
  }

}
