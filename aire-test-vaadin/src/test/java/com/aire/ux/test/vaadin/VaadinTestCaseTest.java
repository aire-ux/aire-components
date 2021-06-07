package com.aire.ux.test.vaadin;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import com.aire.ux.test.AireTest;
import com.aire.ux.test.Navigate;
import com.aire.ux.test.Routes;
import com.aire.ux.test.Select;
import com.aire.ux.test.ViewTest;
import com.aire.ux.test.vaadin.scenarios.routes.MainLayout;

@AireTest
@Routes(scanPackage = "com.aire.ux.test.vaadin.scenarios.routes")
public class VaadinTestCaseTest {

  @ViewTest
  @Navigate("main")
  void ensureVaadinRootViewCanBeInjected(@Select MainLayout layout) {
    assertNotNull(layout);
  }
}
