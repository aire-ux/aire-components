package io.sunshower.zephyr.ui.components;

import com.vaadin.flow.component.ComponentEvent;

/**
 * this isn't really supposed to be dispatched from the client
 * @param <T> whatever this is the context-menu for
 */
public class ContextMenuStateEvent<T> extends ComponentEvent<ContextMenu<T>> {


  private final T value;
  private final Trigger trigger;


  public ContextMenuStateEvent(ContextMenu<T> source, T value, Trigger trigger) {
    super(source, false);
    this.source = source;
    this.value = value;
    this.trigger = trigger;
  }

  public enum Trigger {
    Opened,
    Closed
  }

}
