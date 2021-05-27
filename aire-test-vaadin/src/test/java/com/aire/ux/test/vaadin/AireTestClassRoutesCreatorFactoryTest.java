package com.aire.ux.test.vaadin;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import com.aire.ux.test.AireTest;
import com.aire.ux.test.Element;
import com.aire.ux.test.Routes;
import com.aire.ux.test.ViewTest;
import com.aire.ux.test.vaadin.scenarios.classroutes.ClassScenarioRouteLayout;

@AireTest
@Routes(packages = "com.aire.ux.test.vaadin.scenarios.classroutes")
public class AireTestClassRoutesCreatorFactoryTest {

  /**
   *
   */
  @ViewTest
  void ensureRouteIsInjectableFromMain(@Element ClassScenarioRouteLayout layout) {
    assertNotNull(layout);
  }

}
