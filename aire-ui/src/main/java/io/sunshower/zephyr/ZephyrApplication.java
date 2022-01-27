package io.sunshower.zephyr;

import io.sunshower.zephyr.configuration.EmbeddedZephyrConfiguration;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Import;

@SpringBootApplication
@Import(EmbeddedZephyrConfiguration.class)
public class ZephyrApplication {

  public static void main(String[] args) {
    SpringApplication.run(ZephyrApplication.class, args);
  }
}
