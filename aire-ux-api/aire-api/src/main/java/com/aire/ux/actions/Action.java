package com.aire.ux.actions;

import com.aire.ux.Registration;
import io.sunshower.lang.events.EventListener;
import io.sunshower.lang.events.EventSource;
import io.sunshower.lang.events.EventType;

public interface Action extends EventSource, AutoCloseable {

  Key getKey();

  boolean isEnabled();

  void setEnabled(boolean enabled);

  default void enable() {
    setEnabled(true);
  }

  default void disable() {
    setEnabled(false);
  }

  default void toggle() {
    if (isEnabled()) {
      disable();
    } else {
      enable();
    }
  }

  void perform();

  Registration addActionEventListener(EventType type, EventListener<ActionEvent<?>> listener);

  void dispose();

  default void close() {
    dispose();
  }
}
