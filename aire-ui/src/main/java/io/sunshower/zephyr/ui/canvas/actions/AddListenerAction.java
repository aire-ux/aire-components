package io.sunshower.zephyr.ui.canvas.actions;

import com.vaadin.flow.component.UI;
import io.sunshower.zephyr.ui.canvas.Model;
import io.sunshower.zephyr.ui.canvas.listeners.ListenerDefinition;
import io.sunshower.zephyr.ui.rmi.ClientResult;
import java.util.function.Supplier;

public class AddListenerAction extends AbstractClientMethodBoundAction<ListenerDefinition> {

  static final String NAME = "actions:vertex:listeners:add";
  static final String METHOD_NAME = "addListener";

  private final ListenerDefinition definition;

  public AddListenerAction(Supplier<UI> supplier, ListenerDefinition value) {
    super(NAME, supplier, METHOD_NAME, String.class, ListenerDefinition.class);
    this.definition = value;
  }

  @Override
  public void undo(Model model) {}

  @Override
  public void redo(Model model) {}

  @Override
  public ClientResult<ListenerDefinition> apply(Model model) {
    model.getCommandManager().apply(this);
    return ClientResult.create(
        ListenerDefinition.class, method.invoke(model.getHost(), definition));
  }
}
