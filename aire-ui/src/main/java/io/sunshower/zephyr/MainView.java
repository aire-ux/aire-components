package io.sunshower.zephyr;

import com.aire.ux.Host;
import com.vaadin.flow.component.HasComponents;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.menubar.MenuBar;
import com.vaadin.flow.component.menubar.MenuBarVariant;
import com.vaadin.flow.component.page.AppShellConfigurator;
import com.vaadin.flow.component.page.Push;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouterLink;
import com.vaadin.flow.server.PWA;
import com.vaadin.flow.shared.communication.PushMode;
import io.sunshower.zephyr.management.ModuleGrid;
import io.sunshower.zephyr.ui.controls.Breadcrumb;
import io.sunshower.zephyr.ui.controls.BreadcrumbNavigation;
import io.sunshower.zephyr.ui.controls.NavigationBarButton;
import io.sunshower.zephyr.ui.controls.NavigationBarButton.MatchMode;
import io.sunshower.zephyr.ui.layout.ApplicationLayout;
import io.sunshower.zephyr.ui.layout.TestRoute;
import java.util.List;
import javax.annotation.security.PermitAll;
import lombok.val;

@Route("")
@Host("main")
@PermitAll
@Push(PushMode.MANUAL)
@PWA(name = "Zephyr Shell for Aire", shortName = "Zephyr")
@Breadcrumb(name = "Home", icon = "vaadin:home")
public class MainView extends ApplicationLayout implements AppShellConfigurator {

  public MainView() {
    super();
  }

  @Override
  protected HasComponents createNavigation() {
    val nav = super.createNavigation();

    val button =
        new NavigationBarButton(
            TestRoute.class, List.of("test"), MatchMode.Contains, VaadinIcon.PAPERCLIP.create());
    nav.add(button);
    return nav;
  }

  protected HasComponents createTop() {
    val topNav = super.createTop();
    topNav.add(new BreadcrumbNavigation());

    val menuBar = new MenuBar();
    menuBar.addThemeVariants(MenuBarVariant.LUMO_SMALL, MenuBarVariant.LUMO_ICON);

    var homeButton = new NavigationBarButton(MainView.class, new Image("images/icon.svg", "Home"));
    homeButton.setClassName("container-end");
    topNav.add(homeButton);

    val item = menuBar.addItem(VaadinIcon.MENU.create());
    val submenu = item.getSubMenu();
    submenu.addItem(new RouterLink("Zephyr", ModuleGrid.class));
    topNav.add(menuBar);
    return topNav;
  }
}
