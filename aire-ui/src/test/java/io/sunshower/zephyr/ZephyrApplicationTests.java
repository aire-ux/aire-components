package io.sunshower.zephyr;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import com.aire.ux.test.Routes;
import io.sunshower.zephyr.management.ZephyrManagementConsoleView;
import io.zephyr.cli.Zephyr;
import io.zephyr.kernel.core.Kernel;
import io.zephyr.kernel.launch.KernelOptions;
import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import lombok.val;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;

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

  @Test
  void ensureKernelHasNecessaryConfigurations(
      @Autowired File kernelHome,
      @Autowired KernelOptions options,
      @Autowired ApplicationArguments arguments) {

    assertNotNull(options);
    assertNotNull(arguments);
    assertNotNull(kernelHome);
  }

  /**
   * we must override the kernel home for this
   *
   * @param kernelHome
   * @param options
   */
  @Test
  void ensureTestHomeDirectoryIsUsed(@Autowired File kernelHome, @Autowired KernelOptions options) {
    assertEquals(kernelHome, options.getHomeDirectory());
  }

  @Test
  void ensureInstallingPluginWorks(@Autowired Zephyr zephyr) {
    assertEquals(1, zephyr.getPlugins().size());
  }

  @Test
  void ensureKernelModuleManagerIsConfiguredProperly(@Autowired Kernel kernel) {
    assertNotNull(kernel.getModuleManager().getModuleLoader());
  }

  @Test
  void ensureZephyrCanInstallPlugin(@Autowired Zephyr zephyr) throws MalformedURLException {
    val url =
        "file:///home/josiah/dev/src/github.com/zephyr/kernel-tests/test-plugins/test-plugin-1/build/libs/test-plugin-1-2.0.93-SNAPSHOT.war";
    zephyr.install(new URL(url));
  }
}
