package io.sunshower.zephyr;

import io.sunshower.zephyr.configuration.EmbeddedZephyrConfiguration;
import io.sunshower.zephyr.configuration.ZephyrCoreConfiguration;
import io.zephyr.api.ModuleActivator;
import io.zephyr.api.ModuleContext;
import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;
import lombok.val;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Import;

@SpringBootApplication
@Import({EmbeddedZephyrConfiguration.class, ZephyrCoreConfiguration.class})
public class ZephyrApplication implements ModuleActivator {

  private static final AtomicReference<ConfigurableApplicationContext> context;

  static {
    context = new AtomicReference<>();
  }

  public static void main(String[] args) {
    context.set(SpringApplication.run(ZephyrApplication.class, args));
  }

  public static ConfigurableApplicationContext getApplicationContext() {
    return context.get();
  }

  @Override
  public void start(ModuleContext moduleContext) throws Exception {
    val application = new SpringApplication();
    application.addPrimarySources(Set.of(ZephyrApplication.class));
    application.addInitializers(new EmbeddingModuleInitializer(moduleContext));
    context.set(application.run());
    //    context.set(SpringApplication.run(ZephyrApplication.class));
  }

  @Override
  public void stop(ModuleContext moduleContext) throws Exception {
    context.get().close();
  }
}
