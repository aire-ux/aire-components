package io.sunshower.zephyr.ui.components;

import io.sunshower.zephyr.ui.canvas.listeners.Location;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
@EqualsAndHashCode
public class ContextMenuEvent<T> {

  public final T source;
  public final Location location;
  private final Type type;

  public ContextMenuEvent(T source, Type type, Location location) {
    this.type = type;
    this.source = source;
    this.location = location;
  }

  public enum Type {
    Opened,
    Closed
  }
}
