package io.sunshower.zephyr.ui.canvas;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.function.SerializableRunnable;
import java.util.ArrayList;
import java.util.List;
import lombok.NonNull;
import lombok.val;

public class CompositeAction implements Action {

  static final String key = "actions:bulk";
  private final List<Action> actions;


  public CompositeAction(Action... actions) {
    this.actions = new ArrayList<>(List.of(actions));
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

  }

  @Override
  public void redo(Model model) {

  }

  @Override
  public void apply(Model model) {
    val current = model.getHost().getUI().orElse(UI.getCurrent());
    if (current == null) {
      attemptToApplyLater(model);
    } else {
      current.access(() -> {
        for (val action : actions) {
          action.apply(model);
        }
      });
    }
  }

  private void attemptToApplyLater(Model model) {
    UI.getCurrent().accessLater((SerializableRunnable) () -> {
      for (val action : actions) {
        action.apply(model);
      }
    }, (SerializableRunnable) () -> {

    });

  }
}
