package io.sunshower.zephyr.ui.layout.scenario1;

import com.aire.ux.Host;
import com.aire.ux.Slot;
import com.vaadin.flow.component.html.Main;
import com.vaadin.flow.router.Route;
import io.sunshower.zephyr.ui.layout.ApplicationLayout;
import io.sunshower.zephyr.ui.navigation.NavigationBar;

@Host(":main-2")
@Route(value = "main-navigation", layout = ApplicationLayout.class, registerAtStartup = false)
public class MainNavigationComponent extends Main {

  @Slot(":navbar")
  private NavigationBar navigationBar;

  public MainNavigationComponent() {
    navigationBar = new NavigationBar();
    add(navigationBar);
  }
}
