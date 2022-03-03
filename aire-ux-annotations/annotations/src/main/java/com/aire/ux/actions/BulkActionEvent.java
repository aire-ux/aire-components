package com.aire.ux.actions;

import io.sunshower.lang.events.EventType;
import java.util.List;

public class BulkActionEvent extends AbstractActionEvent<List<Action>> {

  public BulkActionEvent(Key targetKey, List<Action> target,
      EventType eventType) {
    super(targetKey, target, eventType);
  }
}
