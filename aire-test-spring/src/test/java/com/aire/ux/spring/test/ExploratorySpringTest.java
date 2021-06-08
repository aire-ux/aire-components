package com.aire.ux.spring.test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import com.aire.ux.spring.test.scenario1.MainView;
import com.aire.ux.spring.test.scenario1.Scenario1Configuration;
import com.aire.ux.spring.test.scenario1.TestService;
import com.aire.ux.test.AireTest;
import com.aire.ux.test.Context;
import com.aire.ux.test.Navigate;
import com.aire.ux.test.Routes;
import com.aire.ux.test.Select;
import com.aire.ux.test.TestContext;
import com.aire.ux.test.ViewTest;
import com.aire.ux.test.spring.EnableSpring;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.dom.Element;
import javax.inject.Inject;
import lombok.val;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.ContextConfiguration;

@AireTest
@EnableSpring
@Routes(scanClassPackage = TestService.class)
@ContextConfiguration(classes = Scenario1Configuration.class)
public class ExploratorySpringTest {

  @Inject private TestService service;

  @ViewTest
  @Navigate("main")
  void ensureInteractingWithElementWorks(
      @Autowired ApplicationContext applicationContext,
      @Select(".main") Element initialView,
      @Select(".main > vaadin-button") Button button,
      @Context TestContext context) {
    assertNotNull(applicationContext);
    assertEquals(
        0,
        initialView.getComponent().map(component -> (MainView) component).orElseThrow().getCount());
    button.click();
    val view = context.selectFirst(".main", MainView.class).get();
    assertEquals(view.getCount(), 1);
  }

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
