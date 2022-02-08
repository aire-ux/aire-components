package io.sunshower.zephyr.ui.canvas.actions;

import com.vaadin.flow.component.UI;
import io.sunshower.zephyr.ui.canvas.Model;
import io.sunshower.zephyr.ui.canvas.listeners.ListenerDefinition;
import io.sunshower.zephyr.ui.rmi.Argument;
import io.sunshower.zephyr.ui.rmi.ClientResult;
import java.util.List;
import java.util.function.Supplier;

@Argument(collection = true, type = ListenerDefinition.class)
public class AddListenersAction extends AbstractClientMethodBoundAction<List<ListenerDefinition>> {

  static final String NAME = "actions:vertex:listeners:add";
  static final String METHOD_NAME = "addListeners";

  private final List<ListenerDefinition> listeners;

  public AddListenersAction(Supplier<UI> supplier, List<ListenerDefinition> values) {
    super(NAME, supplier, METHOD_NAME, String.class, List.class);
    this.listeners = values;
  }

  @Override
  public void undo(Model model) {

  }

  @Override
  public void redo(Model model) {

  }

  @Override
  public ClientResult<List<ListenerDefinition>> apply(Model model) {
    model.getCommandManager().apply(this);
    return ClientResult.createList(ListenerDefinition.class,
        method.invoke(model.getHost(), listeners));
  }
}
