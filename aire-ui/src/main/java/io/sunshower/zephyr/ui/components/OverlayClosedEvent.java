package io.sunshower.zephyr.ui.components;

import com.vaadin.flow.component.ComponentEvent;

public class OverlayClosedEvent extends ComponentEvent<Overlay> {

  /**
   * is this event the result of closing the overlay instead of acting on it?
   */
  private final boolean cancelled;
  /**
   * Creates a new event using the given source and indicator whether the event originated from the
   * client side or the server side.
   *
   * @param source     the source component
   * @param fromClient <code>true</code> if the event originated from the client
   */
  public OverlayClosedEvent(
      Overlay source,
      boolean fromClient,
      boolean cancelled
  ) {
    super(source, fromClient);
    this.cancelled = cancelled;
  }

  public OverlayClosedEvent(Overlay source, boolean cancelled) {
    this(source, false, cancelled);
  }

  public boolean isCancelled() {
    return cancelled;
  }
}
