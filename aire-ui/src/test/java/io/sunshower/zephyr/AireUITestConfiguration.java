package io.sunshower.zephyr;

import io.sunshower.test.common.Tests;
import io.sunshower.zephyr.configuration.FileProvider;
import io.zephyr.kernel.Module.Type;
import io.zephyr.kernel.core.ModuleCoordinate;
import io.zephyr.kernel.core.ModuleDescriptor;
import io.zephyr.kernel.memento.Memento;
import java.io.File;
import java.util.List;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

@Configuration
public class AireUITestConfiguration {

  @Bean
  @Primary
  public static FileProvider fileProvider() {
    return Tests::createTemp;
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

}
