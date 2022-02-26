package io.sunshower.zephyr.ui.canvas;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface CommandManager {

  Model getModel();

  Canvas getCanvas();

  Optional<RemoteAction> getPrevious();

  boolean hasPrevious();

  boolean hasNext();

  void undo();

  void redo();

  void apply(RemoteAction canvasAction);

  void addPendingAction(RemoteAction canvasAction);

  List<RemoteAction> getPendingActions();

  void setPendingActions(Collection<? extends RemoteAction> actions);

  void applyPendingActions(boolean makeUndoable);

  void clearPendingActions();
}
