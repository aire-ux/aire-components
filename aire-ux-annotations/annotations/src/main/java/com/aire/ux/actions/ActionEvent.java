package com.aire.ux.actions;

import io.sunshower.lang.events.Event;
import io.sunshower.lang.events.EventType;

public interface ActionEvent<E> extends Event<E> {

  Key getTargetKey();

  EventType getEventType();


  enum Type implements EventType {
    ActionEnabled,
    ActionDisabled,
    ActionRegistered, BulkActionsDisabled, BulkActionsEnabled;


    final int id;

    Type() {
      this.id = EventType.newId();
    }

    @Override
    public int getId() {
      return id;
    }
  }
}
