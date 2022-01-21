package io.sunshower.zephyr.management;

import io.sunshower.zephyr.ui.components.Panel;
import io.zephyr.cli.Zephyr;
import io.zephyr.kernel.Coordinate;
import java.util.Optional;
import java.util.function.Supplier;
import lombok.val;

public class ModuleInfoPanel extends Panel {

  private final Zephyr zephyr;

  public ModuleInfoPanel(final Zephyr zephyr, final Supplier<Coordinate> coordinate) {
    this.zephyr = zephyr;
    val opt = Optional.ofNullable(coordinate.get());
    if (opt.isPresent()) {
      add(opt.get().toCanonicalForm());
    } else {
      add("Please select a value");
    }
  }
}
