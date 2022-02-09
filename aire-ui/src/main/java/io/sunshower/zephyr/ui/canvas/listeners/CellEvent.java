package io.sunshower.zephyr.ui.canvas.listeners;

import io.sunshower.lang.events.Event;
import io.sunshower.zephyr.ui.canvas.Canvas;
import io.sunshower.zephyr.ui.canvas.Cell;

public interface CellEvent<T extends Cell> extends Event<T> {

  T getTarget();

  Canvas getSource();

  Location getLocation();
}
