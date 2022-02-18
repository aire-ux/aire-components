package io.sunshower.zephyr.ui.canvas.actions;

import com.vaadin.flow.component.UI;
import io.sunshower.zephyr.ui.canvas.CellAttributes;
import io.sunshower.zephyr.ui.canvas.Model;
import io.sunshower.zephyr.ui.rmi.AbstractClientMethodBoundAction;
import io.sunshower.zephyr.ui.rmi.Argument;
import io.sunshower.zephyr.ui.rmi.ClientResult;
import java.util.List;
import java.util.function.Supplier;

@Argument(collection = true, type = CellAttributes.class)
public class SetAllCellAttributesAction
    extends AbstractClientMethodBoundAction<List<CellAttributes>, Model> {

  static final String NAME = "actions:cell:attributes:set";
  static final String METHOD_NAME = "setAllCellAttributes";
  private final List<CellAttributes> attributes;

  public SetAllCellAttributesAction(Supplier<UI> supplier, List<CellAttributes> attributes) {
    super(NAME, supplier, METHOD_NAME, String.class, List.class);
    this.attributes = attributes;
  }

  @Override
  public void undo(Model model) {}

  @Override
  public void redo(Model model) {}

  @Override
  public ClientResult<List<CellAttributes>> apply(Model model) {
    model.getCommandManager().apply(this);
    return ClientResult.createList(
        CellAttributes.class, method.invoke(model.getHost(), attributes));
  }
}
