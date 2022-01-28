package io.sunshower.zephyr.ui.canvas;

import static org.junit.jupiter.api.Assertions.*;

import com.aire.ux.test.View;
import com.aire.ux.test.ViewTest;
import io.sunshower.zephyr.AireUITest;

@AireUITest
class CanvasTest {

  @ViewTest
  void ensureCanvasIsConstructable(@View Canvas canvas) {
    assertNotNull(canvas);
  }


}