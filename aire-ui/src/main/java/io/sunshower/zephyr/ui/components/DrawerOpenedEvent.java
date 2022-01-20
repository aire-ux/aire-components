package io.sunshower.zephyr.ui.components;

import com.vaadin.flow.component.ComponentEvent;
import com.vaadin.flow.component.DomEvent;

@DomEvent("drawer-opened")
public class DrawerOpenedEvent extends ComponentEvent<Drawer> {

  /**
   * Creates a new event using the given source and indicator whether the event originated from the
   * client side or the server side.
   *
   * @param source     the source component
   * @param fromClient <code>true</code> if the event originated from the client
   */
  public DrawerOpenedEvent(Drawer source, boolean fromClient) {
    super(source, fromClient);
  }
}
