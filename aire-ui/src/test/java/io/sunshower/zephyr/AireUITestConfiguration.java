package io.sunshower.zephyr;

import io.sunshower.test.common.Tests;
import io.zephyr.kernel.Module.Type;
import io.zephyr.kernel.core.ModuleClasspath;
import io.zephyr.kernel.core.ModuleCoordinate;
import io.zephyr.kernel.core.ModuleDescriptor;
import io.zephyr.kernel.memento.Memento;
import io.zephyr.spring.embedded.EmbeddedModuleClasspath;
import io.zephyr.spring.embedded.EmbeddedModuleLoader;
import java.io.File;
import java.util.List;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AireUITestConfiguration {

  @Bean
  public static File kernelRootDirectory() {
    return Tests.createTemp();
  }

  @Bean
  public static Memento memento() {
    return Memento.loadProvider(AireUITest.class.getClassLoader()).newMemento();
  }

  @Bean
  public static ModuleDescriptor moduleDescriptor(File kernelRootDirectory) throws Exception {
    return new ModuleDescriptor(
        kernelRootDirectory.toURI().toURL(),
        0,
        kernelRootDirectory,
        Type.Plugin,
        ModuleCoordinate.create("aire-ui", "com.aire.ux", "1.0.0"),
        List.of(),
        List.of(),
        "test module descriptor");
  }

  @Bean
  public static ModuleClasspath moduleClasspath(ApplicationContext context) {
    return new EmbeddedModuleClasspath(
        new EmbeddedModuleLoader(context.getClassLoader()), context.getClassLoader());
  }
}
