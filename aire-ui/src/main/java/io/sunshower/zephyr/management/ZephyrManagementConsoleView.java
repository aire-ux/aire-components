package io.sunshower.zephyr.management;

import com.vaadin.flow.component.applayout.AppLayout;
import com.vaadin.flow.component.applayout.DrawerToggle;
import com.vaadin.flow.router.Route;

@Route("zephyr/management")
public class ZephyrManagementConsoleView extends AppLayout {

  public ZephyrManagementConsoleView() {
    addToNavbar(new DrawerToggle());
  }
}
