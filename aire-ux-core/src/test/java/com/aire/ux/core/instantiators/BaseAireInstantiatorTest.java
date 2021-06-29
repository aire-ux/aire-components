package com.aire.ux.core.instantiators;


import static org.junit.jupiter.api.Assertions.assertEquals;

import com.aire.ux.core.instantiators.BaseAireInstantiatorTest.Cfg;
import com.aire.ux.core.instantiators.scenario1.MainView;
import com.aire.ux.test.AireTest;
import com.aire.ux.test.Routes;
import com.aire.ux.test.Select;
import com.aire.ux.test.ViewTest;
import com.aire.ux.test.spring.EnableSpring;
import com.vaadin.flow.di.Instantiator;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.ContextConfiguration;

@AireTest
@EnableSpring
@ContextConfiguration(classes = Cfg.class)
@Routes(scanPackage = "com.aire.ux.core.instantiators.scenario1")
class BaseAireInstantiatorTest {


  @ViewTest
  void ensureViewIsInjected(@Select MainView view) {
    assertEquals("world", view.getElement().getProperty("hello"));
  }

  @Configuration
  public static class Cfg {

  }

}