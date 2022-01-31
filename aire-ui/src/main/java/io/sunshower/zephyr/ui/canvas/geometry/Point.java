package io.sunshower.zephyr.ui.canvas.geometry;

import com.google.common.primitives.Doubles;
import lombok.Data;
import lombok.NonNull;

@Data
public final class Point implements Comparable<Point> {

  final double x;
  final double y;

  @Override
  public int compareTo(@NonNull Point point) {
    return -Doubles.compare(y, point.y);
  }
}
