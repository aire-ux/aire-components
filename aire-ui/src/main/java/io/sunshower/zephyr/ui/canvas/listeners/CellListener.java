package io.sunshower.zephyr.ui.canvas.listeners;

import io.sunshower.zephyr.ui.canvas.Cell;

@FunctionalInterface
public interface CellListener<T extends Cell> {

  void on(T cell);
}
