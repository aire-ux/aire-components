package io.sunshower.zephyr;

import io.zephyr.api.ModuleContext;
import io.zephyr.cli.DefaultZephyr;
import io.zephyr.kernel.core.Kernel;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;

@Slf4j
public class EmbeddingModuleInitializer
    implements ApplicationContextInitializer<ConfigurableApplicationContext> {

  private final ModuleContext moduleContext;

  public EmbeddingModuleInitializer(ModuleContext moduleContext) {
    this.moduleContext = moduleContext;
  }

  @Override
  @SuppressWarnings("PMD")
  public void initialize(ConfigurableApplicationContext applicationContext) {
    log.info("Initializing Zephyr-Aire");
    applicationContext.setClassLoader(moduleContext.getModule().getClassLoader());
    val factory = applicationContext.getBeanFactory();
    val kernel = moduleContext.unwrap(Kernel.class);
    log.info("Located kernel: {}", kernel);
    log.info("Registering kernel {} to 'sunshowerKernel'", kernel);
    factory.registerSingleton("sunshowerKernel", kernel);
    factory.registerSingleton("module", moduleContext.getModule());
    factory.registerSingleton("synchronousZephyr", new DefaultZephyr(kernel));
    log.info("Done initializing Zephyr-Aire");
  }
}
