package io.sunshower.zephyr;

import io.sunshower.zephyr.configuration.EmbeddedZephyrConfiguration;
import java.util.concurrent.atomic.AtomicReference;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Import;

@SpringBootApplication
@Import(EmbeddedZephyrConfiguration.class)
public class ZephyrApplication {

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
}
