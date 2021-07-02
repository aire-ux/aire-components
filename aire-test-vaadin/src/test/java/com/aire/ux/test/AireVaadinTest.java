package com.aire.ux.test;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import com.vaadin.flow.component.UI;
import org.junit.jupiter.api.Test;

@AireTest
public class AireVaadinTest {

  @Test
  void ensureTestLoads() {
    assertNotNull(UI.getCurrent());
  }

  @ViewTest
  void ensureEmptyViewTestLoads() {
    assertNotNull(UI.getCurrent());
  }

  @ViewTest
  void ensureParametersWork(@Context TestContext ctx) {
    assertNotNull(ctx);
  }
}
