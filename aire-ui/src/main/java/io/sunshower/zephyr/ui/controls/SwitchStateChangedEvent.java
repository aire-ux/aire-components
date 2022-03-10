package io.sunshower.zephyr.ui.controls;

import com.vaadin.flow.component.ComponentEvent;
import com.vaadin.flow.component.DomEvent;
import com.vaadin.flow.component.EventData;
import lombok.Getter;

@DomEvent("switch-value-changed")
public class SwitchStateChangedEvent extends ComponentEvent<Switch> {

  @Getter private final boolean selected;

  public SwitchStateChangedEvent(
      Switch source, boolean fromClient, @EventData("event.detail.selected") boolean selected) {
    super(source, fromClient);
    this.selected = selected;
  }
}
