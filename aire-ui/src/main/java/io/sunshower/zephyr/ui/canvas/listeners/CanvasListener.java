package io.sunshower.zephyr.ui.canvas.listeners;

public interface CanvasListener<U extends CanvasEvent> {

  void on(U event);
}
