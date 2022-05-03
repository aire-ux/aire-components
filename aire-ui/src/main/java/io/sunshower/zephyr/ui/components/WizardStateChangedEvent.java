package io.sunshower.zephyr.ui.components;

import com.vaadin.flow.component.ComponentEvent;

public class WizardStateChangedEvent<E, V> extends ComponentEvent<Wizard<E, V>> {

  private final E next;
  private final E previous;

  /**
   * Creates a new event using the given source and indicator whether the event originated from the
   * client side or the server side.
   *
   * @param source the source component
   * @param fromClient <code>true</code> if the event originated from the client
   */
  public WizardStateChangedEvent(Wizard<E, V> source, boolean fromClient, E previous, E next) {
    super(source, fromClient);
    this.next = next;
    this.previous = previous;
  }
}
