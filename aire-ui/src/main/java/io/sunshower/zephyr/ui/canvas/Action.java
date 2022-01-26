package io.sunshower.zephyr.ui.canvas;

public interface Action {
  String getKey();

  Action merge(Action other);

  void undo(Model model);

  void redo(Model model);

  void apply(Model model);

}
