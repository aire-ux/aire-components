package com.aire.ux.test;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.aire.ux.test.Context.Mode;
import com.vaadin.flow.component.UI;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

@AireTest
public class AireVaadinTest {

  @ViewTest
  void ensureUICantBeMocked(@Context TestContext context) {
    assertThrows(
        IllegalArgumentException.class,
        () -> {
          context.resolve(UI.class, Mode.Mock);
        });
  }

  @ViewTest
  void ensureUICanBeSpied(@Context(mode = Mode.Spy) UI ui) {
    assertTrue(Mockito.mockingDetails(ui).isSpy());
  }

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
