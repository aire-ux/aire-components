package io.sunshower.zephyr.ui.canvas;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Deque;
import java.util.List;
import java.util.Optional;
import lombok.NonNull;
import lombok.val;

public class StackCommandManager implements CommandManager {

  private final Canvas host;
  private final Model model;
  private final List<CanvasAction> pending;
  private final Deque<CanvasAction> undoStack;
  private final Deque<CanvasAction> redoStack;

  public StackCommandManager(@NonNull Model model, @NonNull Canvas host) {
    this.host = host;
    this.model = model;
    this.pending = new ArrayList<>();
    this.undoStack = new ArrayDeque<>();
    this.redoStack = new ArrayDeque<>();
  }

  @Override
  public Model getModel() {
    return model;
  }

  @Override
  public Canvas getCanvas() {
    return host;
  }

  @Override
  public Optional<CanvasAction> getPrevious() {
    if (undoStack.isEmpty()) {
      return Optional.empty();
    }
    return Optional.of(undoStack.peek());
  }

  @Override
  public boolean hasPrevious() {
    return !undoStack.isEmpty();
  }

  @Override
  public boolean hasNext() {
    return !redoStack.isEmpty();
  }

  @Override
  public void undo() {
    if (!hasPrevious()) {
      throw new NoPreviousActionException();
    }
    val action = undoStack.pop();
    action.apply(model);
    redoStack.push(action);
  }

  @Override
  public void redo() {
    if (!redoStack.isEmpty()) {
      val next = redoStack.pop();
      next.apply(model);
      undoStack.push(next);
    }
  }

  @Override
  public void apply(CanvasAction canvasAction) {
    undoStack.push(canvasAction);
  }

  @Override
  public void addPendingAction(CanvasAction canvasAction) {
    pending.add(canvasAction);
  }

  @Override
  public List<CanvasAction> getPendingActions() {
    return Collections.unmodifiableList(pending);
  }

  @Override
  public void setPendingActions(Collection<? extends CanvasAction> actions) {
    clearPendingActions();
    for (val action : actions) {
      addPendingAction(action);
    }
  }

  @Override
  public void applyPendingActions(boolean makeUndoable) {
    if (!pending.isEmpty()) {
      for (val action : pending) {
        if (makeUndoable) {
          apply(action);
        } else {
          action.apply(model);
        }
      }
    }
  }

  @Override
  public void clearPendingActions() {
    pending.clear();
  }
}
