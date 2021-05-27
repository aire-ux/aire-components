package com.aire.ux.test.vaadin;


import static org.junit.jupiter.api.Assertions.assertNotNull;

import com.aire.ux.test.Element;
import com.aire.ux.test.ViewTest;
import com.aire.ux.test.vaadin.scenarios.routes.MainLayout;
import org.junit.jupiter.api.extension.ExtendWith;


@ExtendWith(VaadinExtension.class)
public class VaadinTestCaseTest {


  @ViewTest
  void ensureVaadinRootViewCanBeInjected(@Element MainLayout layout) {
    assertNotNull(layout);
  }

}
