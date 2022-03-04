package com.aire.ux.actions;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import com.aire.ux.actions.ActionEvent.Type;
import io.sunshower.lang.events.EventListener;
import lombok.val;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class AbstractActionTest {

  private AbstractAction action;

  @BeforeEach
  void setUp() {
    action = new AbstractAction(Key.of("test.action"), true) {};
  }

  @Test
  void ensureActionDisabledEventIsDispatched() {
    val listener = mock(EventListener.class);
    val reg = action.addActionEventListener(Type.ActionEnabled, listener);
    action.setEnabled(true);
    verify(listener).onEvent(eq(Type.ActionEnabled), any());
    reg.close();
  }
}
