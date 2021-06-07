import static org.junit.jupiter.api.Assertions.assertTrue;

import com.aire.ux.test.AireTest;
import com.aire.ux.test.Context;
import com.aire.ux.test.Navigate;
import com.aire.ux.test.Routes;
import com.aire.ux.test.TestContext;
import com.aire.ux.test.ViewTest;
import com.aire.ux.test.vaadin.scenarios.classroutes.ClassScenarioRouteLayout;
import com.aire.ux.test.vaadin.scenarios.routes.MainLayout;
import java.util.logging.Level;
import java.util.logging.Logger;
import lombok.val;
import org.junit.jupiter.api.Order;

@AireTest
@Navigate("test")
@Routes(scanClassPackage = ClassScenarioRouteLayout.class)
public class NavigationTest {

  static {
    Logger.getGlobal().setLevel(Level.ALL);
  }


  @Order(1)
  @ViewTest
  void ensureTestContextBeginsOnEnclosingNavigationTarget(@Context TestContext context) {
    assertTrue(context.selectFirst(ClassScenarioRouteLayout.class).isPresent());
  }

  @Order(2)
  @ViewTest
  @Navigate("main")
  @Routes(scanClassPackage = MainLayout.class)
  void ensureRouteIsAvailable(@Context TestContext context) {
    val layout = context.selectFirst(MainLayout.class);
    assertTrue(layout.isPresent());
  }

  @Order(3)
  @ViewTest
  void ensureTestContextBeginsOnEnclosingNavigationTarget2(@Context TestContext context) {
    assertTrue(context.selectFirst(ClassScenarioRouteLayout.class).isPresent());
  }
}
