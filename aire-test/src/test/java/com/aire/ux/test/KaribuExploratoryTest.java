package com.aire.ux.test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;

import com.aire.ux.test.scenarios.exploratory.FirstRoute;
import com.aire.ux.test.scenarios.exploratory.SecondRoute;
import com.github.mvysny.kaributesting.v10.MockVaadin;
import com.github.mvysny.kaributesting.v10.Routes;
import com.vaadin.flow.component.UI;
import lombok.val;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class KaribuExploratoryTest {

  private static Routes routes = null;

  @BeforeAll
  static void configureScenario() {
    routes = new Routes();
    routes.autoDiscoverViews(FirstRoute.class.getPackageName());
  }

  @BeforeEach
  void configureTests() {
    MockVaadin.setup(routes);
  }

  @Test
  void ensureObtainingRouteWorks() {
    val classes = routes.getRoutes();
    assertEquals(2, classes.size());
  }

  @Test
  void ensureNavigatingToRouteWorks() {
    UI.getCurrent().navigate("firstRoute");
    var element = UI.getCurrent().getElement().getChild(0).getComponent().get();
    assertInstanceOf(FirstRoute.class, element);

    UI.getCurrent().navigate("secondRoute");
    element = UI.getCurrent().getElement().getChild(0).getComponent().get();
    assertInstanceOf(SecondRoute.class, element);
  }
}
