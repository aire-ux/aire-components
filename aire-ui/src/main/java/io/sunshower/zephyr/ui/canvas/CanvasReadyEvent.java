package io.sunshower.zephyr.ui.canvas;

import com.vaadin.flow.component.ComponentEvent;
import com.vaadin.flow.component.DomEvent;

@DomEvent("canvas-ready")
public class CanvasReadyEvent extends ComponentEvent<Canvas> {

  /**
   * Creates a new event using the given source and indicator whether the event originated from the
   * client side or the server side.
   *
   * @param source the source component
   * @param fromClient <code>true</code> if the event originated from the client
   */
  public CanvasReadyEvent(Canvas source, boolean fromClient) {
    super(source, fromClient);
  }
}
