package io.sunshower.zephyr.ui.canvas;

import io.sunshower.zephyr.ui.rmi.ClientResult;

public interface Action<T> {
  String getKey();

  void undo(Model model);

  void redo(Model model);

  ClientResult<T> apply(Model model);
}