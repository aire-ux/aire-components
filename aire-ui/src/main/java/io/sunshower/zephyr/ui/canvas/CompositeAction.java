package io.sunshower.zephyr.ui.canvas;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.function.SerializableRunnable;
import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import lombok.NonNull;
import lombok.val;

public class CompositeAction implements Action {

  static final String key = "actions:bulk";
  private final List<Action> actions;

  public CompositeAction(Action... actions) {
    this(Arrays.stream(actions));
  }

  public CompositeAction(Stream<Action> actions) {
    this.actions = actions.collect(Collectors.toList());
  }

  @Override
  public String getKey() {
    return key;
  }

  @Override
  public Action merge(@NonNull Action other) {
    this.actions.add(other);
    return this;
  }

  @Override
  public void undo(Model model) {
    val current = model.getHost().getUI().orElse(UI.getCurrent());
    if (current == null) {
      attemptToApplyLater(action -> action.undo(model));
    } else {
      current.access(
          () -> {
            for (val action : actions) {
              action.undo(model);
            }
          });
    }
  }

  @Override
  public void redo(Model model) {
    val current = model.getHost().getUI().orElse(UI.getCurrent());
    if (current == null) {
      attemptToApplyLater(action -> action.redo(model));
    } else {
      current.access(
          () -> {
            for (val action : actions) {
              action.redo(model);
            }
          });
    }
  }

  @Override
  public void apply(Model model) {
    val current = model.getHost().getUI().orElse(UI.getCurrent());
    if (current == null) {
      attemptToApplyLater(action -> action.apply(model));
    } else {
      current.access(
          () -> {
            for (val action : actions) {
              action.apply(model);
            }
          });
    }
  }

  private void attemptToApplyLater(Consumer<Action> a) {
    UI.getCurrent()
        .accessLater(
            (SerializableRunnable)
                () -> {
                  for (val action : actions) {
                    a.accept(action);
                  }
                },
            (SerializableRunnable) () -> {});
  }
}
