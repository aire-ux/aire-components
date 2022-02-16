package io.sunshower.zephyr.management;

import com.vaadin.flow.component.AttachEvent;
import com.vaadin.flow.component.DetachEvent;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import io.sunshower.zephyr.ui.components.Drawer;
import io.sunshower.zephyr.ui.components.Drawer.Direction;
import io.sunshower.zephyr.ui.components.Panel;
import io.sunshower.zephyr.ui.controls.DrawerNavigationBarButton;
import io.sunshower.zephyr.ui.layout.Layouts;
import io.sunshower.zephyr.ui.navigation.NavigationBar;
import io.zephyr.cli.Zephyr;
import io.zephyr.kernel.Module;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NonNull;
import lombok.val;

public abstract class AbstractModuleView extends VerticalLayout {


  /**
   * immutable state
   */
  @NonNull
  @Getter(AccessLevel.PROTECTED)
  private final Zephyr zephyr;

  /**
   * right-hand drawer
   */
  @NonNull
  @Getter(AccessLevel.PROTECTED)
  private final Drawer drawer;


  /**
   * the right-hand navigation bar
   */
  @NonNull
  @Getter(AccessLevel.PROTECTED)
  private final NavigationBar navigationBar;

  /**
   * the open action for the drawer
   */
  @NonNull
  @Getter(AccessLevel.PROTECTED)
  private final DrawerNavigationBarButton moduleOpenAction;

  /**
   * mutable state
   */
  protected Module selectedModule;

  protected AbstractModuleView(@NonNull final Zephyr zephyr) {
    configureStyles();
    this.zephyr = zephyr;
    this.drawer = createDrawer();
    this.navigationBar = createNavigationBar();
    this.moduleOpenAction = createModuleOpenAction();
  }

  /**
   * @return the drawer for this module view
   */
  protected Drawer createDrawer() {
    return new Drawer(Direction.VerticalRight);
  }
  /**
   * protected members
   */

  /**
   * @return the navigation bar for this module view
   */
  protected NavigationBar createNavigationBar() {
    val navigationBar = new NavigationBar(NavigationBar.Direction.Vertical);
    navigationBar.setDrawer(drawer);
    return navigationBar;
  }

  /**
   * add the navigation bar to the panel
   *
   * @param panel the panel to set as the navigation bar's contents
   */
  protected void addNavigation(Panel panel) {
    panel.setNavigationBar(navigationBar);
  }

  /**
   * add the navigation when this is attached
   *
   * @param event the attach event
   */

  @Override
  protected void onAttach(AttachEvent event) {
    super.onAttach(event);
    Layouts.locateFirst(event, Panel.class).ifPresent(this::addNavigation);
  }

  /**
   * remove the navigation when this is detached
   *
   * @param event the event
   */
  @Override
  protected void onDetach(DetachEvent event) {
    super.onDetach(event);
    Layouts.locateFirst(event, Panel.class)
        .ifPresent(panel -> panel.removeNavigationBar(navigationBar));
  }

  /**
   * @return the navigation bar button for the right-hand drawer
   */
  protected DrawerNavigationBarButton createModuleOpenAction() {
    var button =
        new DrawerNavigationBarButton(
            VaadinIcon.INFO.create(),
            "Module Info",
            getDrawer(),
            () -> new ModuleInfoPanel(getZephyr(), this::getSelectedModule,
                getModuleLifecycleDelegate()));
    getNavigationBar().add(button);
    return button;
  }

  /**
   * default styles
   */
  protected void configureStyles() {
    val style = this.getStyle();
    style.set("display", "flex");
    style.set("justify-content", "center");
    style.set("align-items", "center");
    this.setHeight("100%");
  }

  protected abstract Module getSelectedModule();

  protected void setSelectedModule(Module module) {
    val drawer = getDrawer();
    if (module != null) {
      drawer.open();
      drawer.setContent(
          new ModuleInfoPanel(getZephyr(), () -> module, getModuleLifecycleDelegate()));
    } else {
      drawer.removeAll();
      drawer.close();
    }
    selectedModule = module;
  }

  /**
   * @return the module lifecycle delegate for the menu
   */
  protected abstract ModuleLifecycleDelegate getModuleLifecycleDelegate();

}
