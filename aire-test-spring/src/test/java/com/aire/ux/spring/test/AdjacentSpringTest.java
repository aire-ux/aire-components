package com.aire.ux.spring.test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import com.aire.ux.spring.test.scenario2.MainView;
import com.aire.ux.spring.test.scenario2.Scenario2Configuration;
import com.aire.ux.spring.test.scenario2.TestService;
import com.aire.ux.test.AireTest;
import com.aire.ux.test.Navigate;
import com.aire.ux.test.RouteLocation;
import com.aire.ux.test.Select;
import com.aire.ux.test.ViewTest;
import com.aire.ux.test.spring.EnableSpring;
import com.vaadin.flow.dom.Element;
import javax.inject.Inject;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;

/** just want to be sure that running tests against different scenarios works */
@AireTest
@EnableSpring
@RouteLocation(scanClassPackage = com.aire.ux.spring.test.scenario2.TestService.class)
@ContextConfiguration(classes = Scenario2Configuration.class)
public class AdjacentSpringTest {

  @Inject private TestService service;

  @Test
  void ensureServiceIsInjected() {
    assertNotNull(service);
  }

  @ViewTest
  @Navigate("main")
  void ensureSpringValueIsInjected(@Select MainView view) {
    assertNotNull(service);
    assertNotNull(view.getService());
  }

  @ViewTest
  @Navigate("main")
  void ensureInjectingCssSelectedValueWorks(
      @Autowired TestService service, @Select(".main") Element mainView) {
    assertNotNull(service);
    assertNotNull(mainView);
    assertEquals(mainView.getComponent().get().getClass(), MainView.class);
  }
}
