package io.sunshower.zephyr.ui.canvas.geometry;

import java.util.stream.Collectors;
import java.util.stream.IntStream;
import lombok.val;

public class RegularPolygon {

  private static final Point origin;

  static {
    origin = new Point(0, 0);
  }

  private final int sides;

  public RegularPolygon(final int sides) {
    this.sides = sides;
  }

  public Path unit() {
    return generate(1d);
  }

  public Path generate(double circumradius) {
    return generate(origin, circumradius);
  }

  public Path generate(Point center, double circumradius) {
    val theta = 360d / sides;
    return new Path(
        IntStream.range(0, sides)
            .mapToObj(
                i ->
                    new Point(
                        center.x + circumradius * Math.sin((i * theta)),
                        center.y + circumradius * Math.cos((i * theta))))
            .collect(Collectors.toList()));
  }
}
