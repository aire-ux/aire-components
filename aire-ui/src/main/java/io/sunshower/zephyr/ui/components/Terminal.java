package io.sunshower.zephyr.ui.components;

import com.vaadin.flow.component.HtmlContainer;
import com.vaadin.flow.component.Tag;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.dependency.JsModule;
import com.vaadin.flow.component.dependency.NpmPackage;
import io.sunshower.zephyr.ui.components.actions.terminal.FlushTerminalAction;
import io.sunshower.zephyr.ui.rmi.ClientMethods;

@Tag("aire-terminal")
@JsModule("xterm/lib/xterm.js")
@JsModule("./aire/ui/components/terminal.ts")
@NpmPackage(value = "xterm", version = "4.17.0")
@NpmPackage(value = "xterm-addon-fit", version = "0.5.0")
public class Terminal extends HtmlContainer {

  public static final int DEFAULT_BUFFER_SIZE = 100;

  private final int bufferSize;
  private final TerminalBuffer buffer;

  public Terminal() {
    this(DEFAULT_BUFFER_SIZE);
  }

  public Terminal(int bufferSize) {
    this.bufferSize = bufferSize;
    this.buffer = new TerminalBuffer(bufferSize);
  }

  public void write(String... lines) {
    this.buffer.write(lines);
    flush();
  }

  public void flush() {
    ClientMethods.withUiSupplier(() -> getUI().orElse(UI.getCurrent()))
        .construct(FlushTerminalAction.class, buffer)
        .apply(this);
    buffer.flush();
  }
}
