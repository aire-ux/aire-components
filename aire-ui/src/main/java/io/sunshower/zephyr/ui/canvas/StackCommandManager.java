package io.sunshower.zephyr.ui.canvas;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Optional;
import lombok.NonNull;
import lombok.val;

public class StackCommandManager implements CommandManager {

  private final Canvas host;
  private final Model model;
  private final Deque<Action> undoStack;
  private final Deque<Action> redoStack;

  public StackCommandManager(@NonNull Model model, @NonNull Canvas host) {
    this.host = host;
    this.model = model;
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
  public Optional<Action> getPrevious() {
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
  public void apply(Action action) {
    undoStack.push(action);
    action.apply(model);
  }
}
