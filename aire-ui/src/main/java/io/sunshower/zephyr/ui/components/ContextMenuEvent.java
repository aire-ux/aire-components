package io.sunshower.zephyr.ui.components;

public interface ContextMenuEvent<T> {

  /**
   * @return whatever triggered this
   */
  T getSource();

  /**
   * @return the x coordinate of this event
   */
  double getX();

  /**
   * @return
   */
  double getY();

  enum Type {
    Opened,
    Closed
  }

}
