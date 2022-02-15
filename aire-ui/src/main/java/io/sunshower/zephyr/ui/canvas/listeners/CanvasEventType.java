package io.sunshower.zephyr.ui.canvas.listeners;

import io.sunshower.zephyr.ui.canvas.Cell;
import java.util.concurrent.atomic.AtomicInteger;
import lombok.NonNull;

public interface CanvasEventType {

  AtomicInteger keySequence = new AtomicInteger();

  static int nextKey() {
    return keySequence.incrementAndGet();
  }

  Cell.Type getCellType();

  @NonNull
  String getType();

  @NonNull
  String getMappedName();

  int getKey();

  String getCategory();
}
