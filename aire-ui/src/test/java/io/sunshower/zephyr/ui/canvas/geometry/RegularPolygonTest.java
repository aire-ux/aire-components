package io.sunshower.zephyr.ui.canvas.geometry;

import lombok.val;
import org.junit.jupiter.api.Test;

class RegularPolygonTest {

  @Test
  void ensureRegularPolygonWorks() {
    val hex = new RegularPolygon(6);
    System.out.println(hex.generate(new Point(25, 25), 25).toPath());
  }
}
