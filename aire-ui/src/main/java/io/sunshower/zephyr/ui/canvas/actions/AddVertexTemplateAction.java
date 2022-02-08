package io.sunshower.zephyr.ui.canvas.actions;

import com.vaadin.flow.component.UI;
import io.sunshower.zephyr.ui.canvas.Model;
import io.sunshower.zephyr.ui.canvas.VertexTemplate;
import io.sunshower.zephyr.ui.rmi.ClientResult;
import java.util.function.Supplier;

public class AddVertexTemplateAction extends AbstractClientMethodBoundAction<VertexTemplate> {

  static final String NAME = "actions:vertices:templates:add";
  static final String METHOD_NAME = "addVertexTemplate";
  private final VertexTemplate value;

  public AddVertexTemplateAction(Supplier<UI> supplier, VertexTemplate value) {
    super(NAME, supplier, METHOD_NAME, String.class, VertexTemplate.class);
    this.value = value;
  }

  @Override
  public void undo(Model model) {
    new RemoveVertexTemplateAction(getSupplier(), value).apply(model);
  }

  @Override
  public void redo(Model model) {}

  @Override
  public ClientResult<VertexTemplate> apply(Model model) {
    model.getCommandManager().apply(this);
    model.addVertexTemplate(value);
    return ClientResult.create(VertexTemplate.class, method.invoke(model.getHost(), value));
  }
}
