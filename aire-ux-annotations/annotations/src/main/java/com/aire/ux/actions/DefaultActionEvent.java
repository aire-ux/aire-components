package com.aire.ux.actions;

import io.sunshower.lang.events.EventType;

class DefaultActionEvent extends AbstractActionEvent {

  public DefaultActionEvent(Key targetKey, Action target,
      EventType eventType) {
    super(targetKey, target, eventType);
  }

  public DefaultActionEvent(Action target, EventType eventType) {
    this(target.getKey(), target, eventType);
  }
}
