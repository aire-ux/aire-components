package io.sunshower.zephyr;

import com.vaadin.flow.spring.annotation.EnableVaadin;
import io.sunshower.arcus.condensation.Condensation;
import io.sunshower.zephyr.configuration.EmbeddedZephyrConfiguration;
import io.sunshower.zephyr.configuration.ZephyrCoreConfiguration;
import io.zephyr.api.ModuleActivator;
import io.zephyr.api.ModuleContext;
import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;
import lombok.val;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.web.servlet.WebMvcAutoConfiguration;
import org.springframework.boot.autoconfigure.web.servlet.error.ErrorMvcAutoConfiguration;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Import;

@EnableVaadin
@SpringBootApplication(exclude = {ErrorMvcAutoConfiguration.class, WebMvcAutoConfiguration.class})
@Import({ZephyrCoreConfiguration.class, EmbeddedZephyrConfiguration.class})
public class ZephyrApplication extends SpringBootServletInitializer implements ModuleActivator {

  private static final AtomicReference<Condensation> condensation;
  private static final AtomicReference<ConfigurableApplicationContext> context;

  static {
    context = new AtomicReference<>();
    condensation = new AtomicReference<>();
  }

  public static void main(String[] args) {
    val application = new SpringApplication(ZephyrApplication.class);
    context.set(application.run(args));
  }

  public static Condensation getCondensation() {
    if (condensation.get() == null) {
      condensation.set(Condensation.create("json"));
    }
    return condensation.get();
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
    condensation.set(Condensation.create("json"));
  }

  @Override
  public void stop(ModuleContext moduleContext) throws Exception {
    context.get().stop();
    condensation.set(null);
  }
}
