package io.sunshower.zephyr.ui.canvas.geometry;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import lombok.val;

public final class Path implements Iterable<Point> {

  private final List<Point> points;


  Path(final Collection<Point> points) {
    this.points = List.copyOf(points);
  }

  @Override
  public Iterator<Point> iterator() {
    return points.iterator();
  }

  public String toPath() {
    val result = new StringBuilder();
    val iter = iterator();

    while (iter.hasNext()) {
      val pt = iter.next();
      result.append(String.format("%3f,%3f", pt.x, pt.y));
      if (iter.hasNext()) {
        result.append(" ");
      }
    }
    return result.toString();
  }
}
