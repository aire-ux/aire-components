package io.sunshower.zephyr.ui.canvas.geometry;

import lombok.val;
import org.junit.jupiter.api.Test;

class RegularPolygonTest {

  @Test
  void ensureRegularPolygonWorks() {
    val hex = new RegularPolygon(13);
    System.out.println(hex.generate(25).toPath());
  }
}
