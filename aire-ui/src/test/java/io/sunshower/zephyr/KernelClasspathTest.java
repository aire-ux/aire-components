package io.sunshower.zephyr;

import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;

public class KernelClasspathTest {

  @Test
  @SneakyThrows
  void ensureModuleExtractorIsPresent() {
    Class.forName("io.zephyr.spring.ext.SpringModuleAssemblyExtractor");
  }
}
