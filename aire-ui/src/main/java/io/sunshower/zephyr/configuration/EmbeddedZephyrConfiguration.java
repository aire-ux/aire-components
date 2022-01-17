package io.sunshower.zephyr.configuration;

import io.sunshower.zephyr.ZephyrApplication;
import io.zephyr.kernel.Module.Type;
import io.zephyr.kernel.core.ModuleClasspath;
import io.zephyr.kernel.core.ModuleCoordinate;
import io.zephyr.kernel.core.ModuleDescriptor;
import io.zephyr.kernel.core.ModuleScanner;
import io.zephyr.kernel.dependencies.DependencyGraph;
import io.zephyr.kernel.memento.Memento;
import io.zephyr.spring.embedded.EmbeddedModule;
import io.zephyr.spring.embedded.EmbeddedModuleClasspath;
import io.zephyr.spring.embedded.EmbeddedModuleLoader;
import io.zephyr.spring.embedded.EmbeddedSpringConfiguration;
import java.io.File;
import java.util.List;
import java.util.Optional;
import java.util.ServiceLoader;
import java.util.ServiceLoader.Provider;
import lombok.val;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@Import(EmbeddedSpringConfiguration.class)
public class EmbeddedZephyrConfiguration implements
    ApplicationListener<ApplicationReadyEvent> {

  @Bean
  public static FileProvider fileProvider(ApplicationArguments arguments) {
    return new ApplicationArgumentsFileProvider(arguments);
  }

  @Bean
  public static File kernelRootDirectory(FileProvider provider) {
    return provider.createFile();
  }

  @Bean
  public static Memento memento(ApplicationContext context) {
    return Memento.load(context.getClassLoader());
  }

  private static ModuleDescriptor defaultModuleDescriptor(File kernelRootDirectory) {
    try {
      return new ModuleDescriptor(
          kernelRootDirectory.toURI().toURL(),
          0,
          kernelRootDirectory,
          Type.Plugin,
          ModuleCoordinate.create("aire-ui", "com.aire.ux", "1.0.0"),
          List.of(),
          List.of(),
          "test module descriptor");
    } catch (Exception ex) {
      throw new IllegalStateException("Unable to create module descriptor");
    }
  }

  @Bean
  public static ModuleClasspath moduleClasspath(ApplicationContext context) {
    return new EmbeddedModuleClasspath(
        new EmbeddedModuleLoader(context.getClassLoader()), context.getClassLoader());
  }


  @Bean
  public static ModuleDescriptor moduleDescriptor(File kernelRootDirectory) {
    val source = ZephyrApplication.class.getProtectionDomain()
        .getCodeSource().getLocation();
    val file = new File(source.getPath());
    return ServiceLoader.load(ModuleScanner.class)
        .stream().map(Provider::get)
        .map(moduleScanner -> moduleScanner.scan(file, source))
        .flatMap(Optional::stream).findAny()
        .orElseGet(() -> defaultModuleDescriptor(kernelRootDirectory));
  }


  @Override
  public void onApplicationEvent(ApplicationReadyEvent event) {
    val context = event.getApplicationContext();
    val graph = context.getBean(DependencyGraph.class);
    graph.add(context.getBean(EmbeddedModule.class));
  }
}
