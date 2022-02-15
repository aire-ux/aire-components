package io.sunshower.zephyr.ui.canvas.listeners;

import io.sunshower.zephyr.ui.canvas.Canvas;

public interface CanvasEvent {

  Canvas getSource();

  Location getLocation();
}
