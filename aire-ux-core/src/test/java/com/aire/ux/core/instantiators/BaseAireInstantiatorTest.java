package com.aire.ux.core.instantiators;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.aire.ux.core.instantiators.BaseAireInstantiatorTest.Cfg;
import com.aire.ux.core.instantiators.scenario1.MainView;
import com.aire.ux.test.AireTest;
import com.aire.ux.test.Navigate;
import com.aire.ux.test.RouteLocation;
import com.aire.ux.test.Select;
import com.aire.ux.test.ViewTest;
import com.aire.ux.test.spring.EnableSpring;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.ContextConfiguration;

@AireTest
@EnableSpring
@ContextConfiguration(classes = Cfg.class)
@RouteLocation(scanPackage = "com.aire.ux.core.instantiators.scenario1")
class BaseAireInstantiatorTest {

  @ViewTest
  @Navigate("mainview")
  void ensureViewIsInjected(@Select MainView view) {
    assertEquals("world", view.getElement().getProperty("hello"));
  }

  /**
   * debugging OOM
   *
   * @param view the view
   */
  @ViewTest
  @Navigate("mainview")
  void ensureViewIsInjected2(@Select MainView view) {
    assertEquals("world", view.getElement().getProperty("hello"));
  }

  /**
   * debugging OOM
   *
   * @param view the view
   */
  @ViewTest
  @Navigate("mainview")
  void ensureViewIsInjected3(@Select MainView view) {
    assertEquals("world", view.getElement().getProperty("hello"));
  }

  @Configuration
  public static class Cfg {}
}
