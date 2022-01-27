package io.sunshower.zephyr.ui.canvas;

import com.vaadin.flow.component.UI;
import java.util.function.Consumer;
import lombok.val;

public abstract class AbstractUIAccessingAction implements Action {

  private final String key;

  protected AbstractUIAccessingAction(final String key) {
    this.key = key;
  }

  @Override
  public String getKey() {
    return key;
  }

  @Override
  public Action merge(Action other) {
    return null;
  }

  protected UI lookupUi(Canvas canvas) {
    return canvas
        .getUI()
        .orElse(UI.getCurrent())
        .getUI()
        .orElseThrow(() -> new CanvasException("No UI available!"));
  }

  protected Consumer<TriFunction<UI, Canvas, Model, Void>> access(Model model) {
    return f -> {
      val host = model.getHost();
      val ui = lookupUi(host);
      ui.access(
          () -> {
            f.apply(ui, host, model);
          });
    };
  }
}
