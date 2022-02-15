package io.sunshower.zephyr.ui.canvas.listeners;

import io.sunshower.lang.events.Event;
import io.sunshower.zephyr.ui.canvas.Canvas;

public class CanvasInteractionEvent implements CanvasEvent, Event<Canvas> {

  private final Canvas source;
  private final Location location;

  public CanvasInteractionEvent(Canvas source, Location location) {
    this.source = source;
    this.location = location;
  }

  @Override
  public Canvas getSource() {
    return source;
  }

  @Override
  public Location getLocation() {
    return location;
  }

  @Override
  public Canvas getTarget() {
    return source;
  }
}
