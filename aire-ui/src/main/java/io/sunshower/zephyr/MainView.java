package io.sunshower.zephyr;

import com.vaadin.flow.component.HasComponents;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.menubar.MenuBar;
import com.vaadin.flow.component.menubar.MenuBarVariant;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouterLink;
import io.sunshower.zephyr.management.ZephyrManagementConsoleView;
import io.sunshower.zephyr.ui.controls.NavigationBarButton;
import io.sunshower.zephyr.ui.layout.ApplicationLayout;
import lombok.val;

@Route("")
public class MainView extends ApplicationLayout {

  public MainView() {

  }

  protected HasComponents createTop() {
    val topNav = super.createTop();
    val menuBar = new MenuBar();
    menuBar.addThemeVariants(MenuBarVariant.LUMO_SMALL, MenuBarVariant.LUMO_ICON);

    var homeButton = new NavigationBarButton(MainView.class, new Image("images/icon.svg", "Home"));
    homeButton.setClassName("container-end");
    topNav.add(homeButton);
    val item = menuBar.addItem(VaadinIcon.MENU.create());
    val submenu = item.getSubMenu();
    submenu.addItem(new RouterLink("Zephyr", ZephyrManagementConsoleView.class));
    topNav.add(menuBar);
    return topNav;
  }
}
