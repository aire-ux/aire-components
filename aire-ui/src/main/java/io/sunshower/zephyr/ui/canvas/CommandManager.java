package io.sunshower.zephyr.ui.canvas;

import java.util.Collection;
import java.util.List;
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

  void addPendingAction(Action action);

  List<Action> getPendingActions();

  void setPendingActions(Collection<? extends Action> actions);

  void applyPendingActions(boolean makeUndoable);

  void clearPendingActions();
}
