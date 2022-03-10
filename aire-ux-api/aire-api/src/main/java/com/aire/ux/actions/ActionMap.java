package com.aire.ux.actions;

import java.util.List;

public interface ActionMap {
  Action remove(Key key);

  void add(Action action);

  List<Action> getKeysIn(Key... path);

  Action get(Key key);
}
