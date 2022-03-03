package com.aire.ux.actions;

import io.sunshower.lang.events.EventSource;

public interface Action extends EventSource {


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
    if(isEnabled()) {
      disable();
    } else {
      enable();
    }
  }



}
