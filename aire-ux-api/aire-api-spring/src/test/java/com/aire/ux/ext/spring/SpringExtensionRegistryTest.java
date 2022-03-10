package com.aire.ux.ext.spring;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.aire.ux.RouteDefinition;
import com.aire.ux.annotations.scenario1.FrontPage;
import com.aire.ux.ext.ExtensionRegistry;
import com.aire.ux.test.AireTest;
import com.aire.ux.test.Context;
import com.aire.ux.test.TestContext;
import com.aire.ux.test.ViewTest;
import com.aire.ux.test.spring.EnableSpring;
import com.vaadin.flow.router.NotFoundException;
import java.util.Set;
import lombok.SneakyThrows;
import lombok.val;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;

@AireTest
@EnableSpring
@ContextConfiguration(classes = TestConfiguration.class)
class SpringExtensionRegistryTest {


  @Test
  void ensureExtensionRegistryIsInjectable(@Autowired ExtensionRegistry registry) {
    assertNotNull(registry);
  }

  @Test
  void ensureRouteDefinitionHashCodeWorks() {
    val def1 = Set.of(RouteDefinition.fromAnnotatedClass(FrontPage.class));
    assertTrue(def1.contains(RouteDefinition.fromAnnotatedClass(FrontPage.class)));

  }

  @ViewTest
  @SneakyThrows
  void ensureRegistryCanRegisterRoutes(@Autowired ExtensionRegistry registry) {
    try(val reg = registry.register(RouteDefinition.fromAnnotatedClass(FrontPage.class))) {
      assertEquals(1, registry.getExtensions().size());
    }
  }

  @ViewTest
  void ensureDisablingTypeWorks(@Autowired ExtensionRegistry registry, @Context TestContext $) {
    try (val reg = registry.register(RouteDefinition.fromAnnotatedClass(FrontPage.class))) {
      val routes = registry.getRouteDefinitions();
      $.navigate(FrontPage.class);
      val registration = registry.getComponentInclusionManager().disableRoutes(routes);
      assertThrows(NotFoundException.class, () -> $.navigate(FrontPage.class));
      registration.close();
      $.navigate(FrontPage.class);
    }
    assertEquals(0, registry.getExtensions().size());
  }


}