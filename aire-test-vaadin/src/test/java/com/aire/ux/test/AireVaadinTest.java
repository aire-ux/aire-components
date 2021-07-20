package com.aire.ux.test;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import com.aire.ux.test.Context.Mode;
import com.vaadin.flow.component.UI;
import java.util.ArrayList;
import lombok.val;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

@AireTest
public class AireVaadinTest {


  @Test
  void ensureResettingWorks() {
    val list = spy(new ArrayList<Integer>());
    list.add(1);
    verify(list, times(1)).add(eq(1));
    reset(list);
    verify(list, times(0)).add(any());

  }


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

  @ViewTest
  void ensureUIMethodCanBeCalled(@Context(mode = Mode.Spy) UI ui) {
    ui.getPage();
    verify(ui, times(1)).getPage();
  }

  @ViewTest
  void ensureUIMethodCanBeCalled2(@Context(mode = Mode.Spy) UI ui) {
    ui.getPage();
    verify(ui, times(1)).getPage();
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
