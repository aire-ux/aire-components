package io.sunshower.zephyr.ui.canvas;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface CommandManager {

  Model getModel();

  Canvas getCanvas();

  Optional<CanvasAction> getPrevious();

  boolean hasPrevious();

  boolean hasNext();

  void undo();

  void redo();

  void apply(CanvasAction canvasAction);

  void addPendingAction(CanvasAction canvasAction);

  List<CanvasAction> getPendingActions();

  void setPendingActions(Collection<? extends CanvasAction> actions);

  void applyPendingActions(boolean makeUndoable);

  void clearPendingActions();
}
