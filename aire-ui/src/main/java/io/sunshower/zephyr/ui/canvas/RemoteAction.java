package io.sunshower.zephyr.ui.canvas;

import io.sunshower.zephyr.ui.rmi.ClientResult;

public interface RemoteAction<T, U> {
  String getKey();

  void undo(U model);

  void redo(U model);

  ClientResult<T> apply(U model);
}
