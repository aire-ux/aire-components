package com.aire.ux.actions;

import io.sunshower.lang.events.EventType;

public class AbstractActionEvent<E> implements ActionEvent<E> {

  final Key targetKey;
  final E target;
  final EventType eventType;

  protected AbstractActionEvent(Key targetKey, E target,
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
  public E getTarget() {
    return target;
  }
}
