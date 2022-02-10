package io.sunshower.zephyr.ui.components;

import com.vaadin.flow.component.ComponentEvent;
import com.vaadin.flow.component.DomEvent;
import com.vaadin.flow.component.EventData;
import lombok.Getter;

@DomEvent("context-menu-state-changed")
public class ContextMenuStateChangedEvent extends ComponentEvent<ContextMenu> {

  @Getter private final boolean closed;

  public ContextMenuStateChangedEvent(
      ContextMenu source, boolean fromClient, @EventData("event.detail.state") boolean closed) {
    super(source, fromClient);
    this.closed = closed;
  }
}
