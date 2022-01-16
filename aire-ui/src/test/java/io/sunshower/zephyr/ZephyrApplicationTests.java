package io.sunshower.zephyr;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import com.aire.ux.test.Navigate;
import com.aire.ux.test.Routes;
import com.aire.ux.test.Select;
import com.aire.ux.test.ViewTest;
import io.sunshower.zephyr.management.ZephyrManagementConsoleView;
import io.zephyr.cli.Zephyr;
import io.zephyr.kernel.core.Kernel;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

@AireUITest
@Routes(scanClassPackage = ZephyrManagementConsoleView.class)
class ZephyrApplicationTests {

  @Test
  void ensureKernelIsInjectable(@Autowired Kernel kernel) {
    assertNotNull(kernel, "kernel must be injectable");
  }

  @Test
  void ensureSynchronousApiIsInjectable(@Autowired Zephyr zephyr) {
    assertNotNull(zephyr, "synchronous api must be injectable");
  }

  @ViewTest
  @Navigate("zephyr/management")
  void ensureManagementUiIsInjectable(@Select ZephyrManagementConsoleView view) {
    assertNotNull(view, "view must be injectable");
  }
}
