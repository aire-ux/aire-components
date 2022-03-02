package com.aire.ux.annotations.ext;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.aire.ux.annotations.ext.ExtensionRegistryTypeAnnotationTest.Cfg;
import com.aire.ux.annotations.ext.scenario1.TestExtension;
import com.aire.ux.annotations.ext.scenario1.TestExtensionPoint;
import com.aire.ux.concurrency.AccessQueue;
import com.aire.ux.ext.ExtensionRegistry;
import com.aire.ux.ext.spring.SpringExtensionRegistry;
import com.aire.ux.test.AireTest;
import com.aire.ux.test.Context;
import com.aire.ux.test.Navigate;
import com.aire.ux.test.RegisterComponentExtension;
import com.aire.ux.test.RegisterExtension;
import com.aire.ux.test.Routes;
import com.aire.ux.test.Select;
import com.aire.ux.test.TestContext;
import com.aire.ux.test.ViewTest;
import com.aire.ux.test.spring.EnableSpring;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.server.Command;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.ContextConfiguration;

@AireTest
@EnableSpring
@ContextConfiguration(classes = Cfg.class)
@ExtendWith(RegisterComponentExtension.class)
@RegisterExtension(TestExtension.class)
@Routes(scanClassPackage = TestExtensionPoint.class)
@Navigate("home")
@Disabled
public class ExtensionRegistryTypeAnnotationTest {

  @ViewTest
  void ensureExtensionIsRegistered(@Context ExtensionRegistry registry) {
    assertNotNull(registry);
  }

  @ViewTest
  void ensureRegistryHasCorrectHostCount(@Context ExtensionRegistry registry) {
    assertEquals(1, registry.getHostCount());
  }

  @ViewTest
  void ensureRegistryHasCorrectExtensionCount(@Context ExtensionRegistry registry) {
    assertEquals(1, registry.getExtensionCount());
  }

  @ViewTest
  @Navigate("home")
  void ensureAppendedItemIsSelectable(
      @Select("vaadin-button") Button button, @Context TestContext ctx) {
    assertNotNull(button);
    assertFalse(ctx.selectFirst(TestExtension.class).isPresent());
    button.click();
    assertTrue(ctx.selectFirst(TestExtension.class).isPresent());
  }

  static class DefaultAccessQueue implements AccessQueue {

    @Override
    public void enqueue(Command command) {
      UI.getCurrent().access(command);
    }
  }

  @Configuration
  public static class Cfg {

    @Bean
    public static ExtensionRegistry extensionRegistry() {
      return new SpringExtensionRegistry(new DefaultAccessQueue());
    }
  }
}
