package io.sunshower.zephyr.ui.components;

import io.sunshower.arcus.condensation.Element;
import io.sunshower.arcus.condensation.RootElement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@RootElement
public class TerminalBuffer {

  private int capacity;
  @Element private List<String> values;

  public TerminalBuffer(final int capacity) {
    values = new ArrayList<>(capacity);
    this.capacity = capacity;
  }

  public TerminalBuffer() {}

  public void flush() {
    if (values != null) {
      values.clear();
    }
  }

  public void write(String[] lines) {
    if (values == null) {
      values = new ArrayList<>(Math.max(capacity, 10));
    }
    values.addAll(Arrays.asList(lines));
  }

  public boolean capacityReached() {
    return values != null && values.size() >= capacity;
  }
}
