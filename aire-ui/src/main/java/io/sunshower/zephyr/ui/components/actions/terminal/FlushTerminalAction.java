package io.sunshower.zephyr.ui.components.actions.terminal;

import com.vaadin.flow.component.UI;
import io.sunshower.zephyr.ui.components.Terminal;
import io.sunshower.zephyr.ui.components.TerminalBuffer;
import io.sunshower.zephyr.ui.rmi.AbstractClientMethodBoundAction;
import io.sunshower.zephyr.ui.rmi.ClientResult;
import java.util.function.Supplier;

public class FlushTerminalAction extends AbstractClientMethodBoundAction<TerminalBuffer, Terminal> {

  static final String NAME = "terminal:data:write-buffer";
  static final String METHOD_NAME = "write";
  private final TerminalBuffer buffer;

  public FlushTerminalAction(Supplier<UI> supplier, TerminalBuffer buffer) {
    super(NAME, supplier, METHOD_NAME, String.class, TerminalBuffer.class);
    this.buffer = buffer;
  }

  @Override
  public void undo(Terminal model) {}

  @Override
  public void redo(Terminal model) {}

  @Override
  public ClientResult<TerminalBuffer> apply(Terminal model) {
    return ClientResult.create(TerminalBuffer.class, method.invoke(model, buffer));
  }
}
