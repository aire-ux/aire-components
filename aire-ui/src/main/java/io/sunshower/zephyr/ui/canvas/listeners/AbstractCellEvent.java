package io.sunshower.zephyr.ui.canvas.listeners;

import io.sunshower.zephyr.ui.canvas.Canvas;
import io.sunshower.zephyr.ui.canvas.Cell;
import lombok.NonNull;
import lombok.ToString;

@ToString
public class AbstractCellEvent<T extends Cell> implements CellEvent<T> {

  private final T target;
  private final Canvas source;
  private final Location location;

  protected AbstractCellEvent(
      @NonNull T target, @NonNull Canvas source, @NonNull Location location) {
    this.target = target;
    this.source = source;
    this.location = location;
  }

  @Override
  public T getTarget() {
    return target;
  }

  @Override
  public Canvas getSource() {
    return source;
  }

  @Override
  public Location getLocation() {
    return location;
  }
}
