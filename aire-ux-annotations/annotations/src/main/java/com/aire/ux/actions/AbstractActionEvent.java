package com.aire.ux.actions;

import io.sunshower.lang.events.EventType;

public class AbstractActionEvent implements ActionEvent {

  final Key targetKey;
  final Action target;
  final EventType eventType;

  protected AbstractActionEvent(Key targetKey, Action target,
      EventType eventType) {
    this.targetKey = targetKey;
    this.target = target;
    this.eventType = eventType;
  }


  @Override
  public Key getTargetKey() {
    return targetKey;
  }

  @Override
  public EventType getEventType() {
    return eventType;
  }

  @Override
  public Action getTarget() {
    return target;
  }
}
