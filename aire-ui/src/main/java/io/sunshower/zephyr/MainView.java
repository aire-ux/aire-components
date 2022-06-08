package io.sunshower.zephyr;

import static org.springframework.security.web.context.HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY;

import com.aire.ux.Host;
import com.vaadin.flow.component.ClickEvent;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.HasComponents;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.contextmenu.MenuItem;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.menubar.MenuBar;
import com.vaadin.flow.component.menubar.MenuBarVariant;
import com.vaadin.flow.component.page.AppShellConfigurator;
import com.vaadin.flow.component.page.Push;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouterLink;
import com.vaadin.flow.server.PWA;
import com.vaadin.flow.server.VaadinSession;
import com.vaadin.flow.shared.communication.PushMode;
import io.sunshower.cloud.studio.components.workspace.WorkspaceListView;
import io.sunshower.zephyr.configuration.SecurityUtils;
import io.sunshower.zephyr.management.ModuleGrid;
import io.sunshower.zephyr.security.views.AuthenticationView;
import io.sunshower.zephyr.ui.controls.Breadcrumb;
import io.sunshower.zephyr.ui.controls.BreadcrumbNavigation;
import io.sunshower.zephyr.ui.controls.NavigationBarButton;
import io.sunshower.zephyr.ui.controls.NavigationBarButton.MatchMode;
import io.sunshower.zephyr.ui.identicon.Identicon;
import io.sunshower.zephyr.ui.layout.ApplicationLayout;
import java.util.List;
import javax.annotation.security.PermitAll;
import javax.inject.Inject;
import lombok.val;
import org.springframework.security.core.context.SecurityContextHolder;

@Route("")
@Host("main")
@PermitAll
@Push(PushMode.MANUAL)
@PWA(name = "Zephyr Shell for Aire", shortName = "Zephyr")
@Breadcrumb(name = "Home", icon = "vaadin:home")
public class MainView extends ApplicationLayout implements AppShellConfigurator {

  @Inject
  public MainView() {
    super();
  }

  @Override
  protected HasComponents createNavigation() {
    val nav = super.createNavigation();

    val button =
        new NavigationBarButton(
            WorkspaceListView.class,
            List.of("workspaces"),
            MatchMode.Contains,
            VaadinIcon.EDIT.create());
    nav.add(button);

    return nav;
  }

  protected HasComponents createTop() {
    val topNav = super.createTop();
    topNav.add(new BreadcrumbNavigation());

    val menuBar = new MenuBar();
    menuBar.addThemeVariants(MenuBarVariant.LUMO_SMALL, MenuBarVariant.LUMO_ICON);

    var homeButton =
        new NavigationBarButton(MainView.class, new Image("/zephyr/images/icon.svg", "Home"));
    homeButton.setClassName("container-end");
    topNav.add(homeButton);

    val item = menuBar.addItem(VaadinIcon.MENU.create());
    val submenu = item.getSubMenu();
    submenu.addItem(new RouterLink("Zephyr", ModuleGrid.class));
    topNav.add(menuBar);

    if (SecurityUtils.isUserLoggedIn()) {
      val user = SecurityContextHolder.getContext().getAuthentication();
      val userIcon = Identicon.createFromObject(user.getPrincipal(), "Current User");
      userIcon.setWidth("24px");
      userIcon.setHeight("24px");
      val userMenu = menuBar.addItem(userIcon);
      val subMenu = userMenu.getSubMenu();
      subMenu.addItem(
          "Log out: " + user.getPrincipal(),
          (ComponentEventListener<ClickEvent<MenuItem>>)
              event -> {
                VaadinSession.getCurrent().setAttribute(SPRING_SECURITY_CONTEXT_KEY, null);
                SecurityContextHolder.clearContext();
                val ui = UI.getCurrent();
                ui.navigate(AuthenticationView.class);
              });
    }

    return topNav;
  }
}
