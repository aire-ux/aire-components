package io.sunshower.zephyr.ui.canvas.actions;

import com.vaadin.flow.component.UI;
import io.sunshower.zephyr.ui.canvas.Model;
import io.sunshower.zephyr.ui.canvas.VertexTemplate;
import io.sunshower.zephyr.ui.rmi.AbstractClientMethodBoundAction;
import io.sunshower.zephyr.ui.rmi.ClientResult;
import java.util.function.Supplier;

public class RemoveVertexTemplateAction
    extends AbstractClientMethodBoundAction<VertexTemplate, Model> {

  static final String NAME = "actions:vertices:templates:remove";
  static final String METHOD_NAME = "removeVertexTemplate";
  private final VertexTemplate value;

  public RemoveVertexTemplateAction(Supplier<UI> supplier, VertexTemplate value) {
    super(NAME, supplier, METHOD_NAME, String.class, VertexTemplate.class);
    this.value = value;
  }

  @Override
  public void undo(Model model) {
    new AddVertexTemplateAction(getSupplier(), value).apply(model);
  }

  @Override
  public void redo(Model model) {}

  @Override
  public ClientResult<VertexTemplate> apply(Model model) {
    return null;
  }
}
