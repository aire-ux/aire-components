package io.sunshower.zephyr.ui.canvas;

import java.util.Optional;

public interface CommandManager {


  Model getModel();

  Canvas getCanvas();

  Optional<Action> getPrevious();

  boolean hasPrevious();

  boolean hasNext();

  void undo();

  void redo();

  void apply(Action action);

}
