package com.aire.ux.annotations.ext;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import com.aire.ux.annotations.ext.ExtensionRegistryTypeAnnotationTest.Cfg;
import com.aire.ux.annotations.ext.scenario1.TestExtension;
import com.aire.ux.ext.ExtensionRegistry;
import com.aire.ux.ext.spring.SpringExtensionRegistry;
import com.aire.ux.test.AireTest;
import com.aire.ux.test.Context;
import com.aire.ux.test.RegisterComponentExtension;
import com.aire.ux.test.RegisterExtension;
import com.aire.ux.test.Routes;
import com.aire.ux.test.ViewTest;
import com.aire.ux.test.spring.EnableSpring;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.ContextConfiguration;

@AireTest
@EnableSpring
@ContextConfiguration(classes = Cfg.class)
@ExtendWith(RegisterComponentExtension.class)
@RegisterExtension(TestExtension.class)
@Routes(scanClassPackage = TestExtension.class)
public class ExtensionRegistryTypeAnnotationTest {

  @ViewTest
  void ensureExtensionIsRegistered(@Context ExtensionRegistry registry) {
    assertNotNull(registry);
  }

  @ViewTest
  void ensureRegistryHasCorrectExtensionCount(@Context ExtensionRegistry registry) {
    assertEquals(1, registry.getDefinitions().size());
  }


  @Configuration
  public static class Cfg {

    @Bean
    public static ExtensionRegistry extensionRegistry() {
      return new SpringExtensionRegistry();
    }
  }

}
