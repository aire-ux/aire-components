package io.sunshower.zephyr.configuration;

import com.aire.ux.Registration;
import io.sunshower.zephyr.ZephyrApplication;
import io.zephyr.kernel.Lifecycle.State;
import io.zephyr.kernel.Module.Type;
import io.zephyr.kernel.concurrency.ExecutorWorkerPool;
import io.zephyr.kernel.concurrency.WorkerPool;
import io.zephyr.kernel.core.Kernel;
import io.zephyr.kernel.core.KernelModuleLoader;
import io.zephyr.kernel.core.ModuleClasspath;
import io.zephyr.kernel.core.ModuleClasspathManager;
import io.zephyr.kernel.core.ModuleCoordinate;
import io.zephyr.kernel.core.ModuleDescriptor;
import io.zephyr.kernel.core.ModuleScanner;
import io.zephyr.kernel.core.SunshowerKernel;
import io.zephyr.kernel.dependencies.DependencyGraph;
import io.zephyr.kernel.memento.Memento;
import io.zephyr.spring.embedded.EmbeddedModule;
import io.zephyr.spring.embedded.EmbeddedModuleClasspath;
import io.zephyr.spring.embedded.EmbeddedModuleLoader;
import io.zephyr.spring.embedded.EmbeddedSpringConfiguration;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.ServiceLoader;
import java.util.ServiceLoader.Provider;
import java.util.concurrent.Executors;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Primary;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

/**
 * this is sort of a dev class. I guess we could move it to tests or something and include a new m
 */
@Slf4j
@EnableAsync
@Configuration
@Import(EmbeddedSpringConfiguration.class)
@ComponentScan(basePackages = "io.sunshower.zephyr.core")
@ConditionalOnMissingBean(Kernel.class)
public class EmbeddedZephyrConfiguration
    implements ApplicationListener<ApplicationReadyEvent>, DisposableBean {

  private final List<Registration> registrations;

  public EmbeddedZephyrConfiguration() {
    registrations = new ArrayList<>();
  }

  @Bean
  public static FileProvider fileProvider(ApplicationArguments arguments) {
    return new ApplicationArgumentsFileProvider(arguments);
  }

  @Bean
  public static File kernelRootDirectory(FileProvider provider) {
    return provider.createFile();
  }

  @Bean
  @SuppressWarnings("PMD.UseProperClassLoader")
  public static Memento memento(ApplicationContext context) {
    return Memento.load(context.getClassLoader());
  }

  @Bean
  @Primary
  public static WorkerPool workerPool(ThreadPoolTaskExecutor executor) {
    return new ExecutorWorkerPool(
        executor.getThreadPoolExecutor(), Executors.newFixedThreadPool(2));
  }

  @Bean
  public static ModuleClasspathManager moduleClasspathManager(
      DependencyGraph graph, Kernel kernel) {
    val result = new KernelModuleLoader(graph, kernel);
    ((SunshowerKernel) kernel).setModuleClasspathManager(result);
    return result;
  }

  private static ModuleDescriptor defaultModuleDescriptor(File kernelRootDirectory) {
    try {
      return new ModuleDescriptor(
          kernelRootDirectory.toURI().toURL(),
          0,
          kernelRootDirectory,
          Type.Plugin,
          ModuleCoordinate.create("com.aire.ux.ui", "aire-ui", "1.0.1"),
          List.of(),
          List.of(),
          "Management UI for Zephyr");
    } catch (Exception ex) {
      throw new IllegalStateException("Unable to create module descriptor");
    }
  }

  @Bean
  @SuppressWarnings("PMD.UseProperClassLoader")
  public static ModuleClasspath moduleClasspath(ApplicationContext context) {
    return new EmbeddedModuleClasspath(
        new EmbeddedModuleLoader(context.getClassLoader()), context.getClassLoader());
  }

  @Bean
  public static ModuleDescriptor moduleDescriptor(File kernelRootDirectory) {
    val source = ZephyrApplication.class.getProtectionDomain().getCodeSource().getLocation();
    val file = new File(source.getPath());
    return ServiceLoader.load(ModuleScanner.class).stream()
        .map(Provider::get)
        .map(moduleScanner -> moduleScanner.scan(file, source))
        .flatMap(Optional::stream)
        .findAny()
        .orElseGet(() -> defaultModuleDescriptor(kernelRootDirectory));
  }

  @Override
  public void onApplicationEvent(ApplicationReadyEvent event) {
    val context = event.getApplicationContext();
    val graph = context.getBean(DependencyGraph.class);
    val module = context.getBean(EmbeddedModule.class);
    try {
      graph.add(context.getBean(EmbeddedModule.class));
      val kernel = context.getBean(Kernel.class);
      module.getLifecycle().setState(State.Starting);
      log.info("Starting embedded kernel...");
      kernel.start();
      log.info("Embedded kernel started successfully.  Started embedded module");
      module.getLifecycle().setState(State.Active);
      log.info("Module started successfully");
    } catch (Exception ex) {
      log.error("Encountered an error attempting to start kernel: {}", ex.getMessage(), ex);
    }
  }

  @Override
  public void destroy() throws Exception {
    synchronized (registrations) {
      val iter = registrations.iterator();
      while (iter.hasNext()) {
        iter.next().close();
        iter.remove();
      }
    }
  }
}
