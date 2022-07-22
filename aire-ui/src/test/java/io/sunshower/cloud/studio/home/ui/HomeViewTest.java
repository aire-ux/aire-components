package io.sunshower.cloud.studio.home.ui;

import static org.junit.jupiter.api.Assertions.*;

import com.aire.ux.test.Navigate;
import com.aire.ux.test.RouteLocation;
import com.aire.ux.test.Select;
import com.aire.ux.test.ViewTest;
import com.vaadin.flow.component.html.H1;
import io.sunshower.zephyr.AireUITest;
import io.sunshower.zephyr.management.ModuleGrid;
import java.util.ResourceBundle;

@AireUITest
@RouteLocation(scanClassPackage = HomeView.class)
@RouteLocation(scanClassPackage = ModuleGrid.class)
class HomeViewTest {

  @ViewTest
  @Navigate("start")
  void ensureHomeViewWorksIsLocalized(@Select H1 h1) {
    assertNotNull(h1);
    assertEquals(
        h1.getText(),
        ResourceBundle.getBundle("i18n." + HomeView.class.getName()).getString("header"));
  }
}
