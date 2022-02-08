package io.sunshower.zephyr.ui.canvas.listeners;

import io.sunshower.zephyr.ui.canvas.Cell;

@FunctionalInterface
public interface CellListener<T extends Cell, U extends CellEvent<T>> {

  void on(U cell);
}
