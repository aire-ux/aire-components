package com.aire.ux.control.canvas;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import com.aire.ux.control.canvas.Canvas.Attributes;
import com.aire.ux.control.designer.servlet.DesignerConfiguration;
import lombok.val;
import org.junit.jupiter.api.Test;

class CanvasTest {

  @Test
  void ensureBasePathIsSet() {
    val canvas = new Canvas();

    assertNotNull(Attributes.BasePath.get(canvas.getElement()));
    assertEquals(
        DesignerConfiguration.getInstance().getBasePath(),
        Attributes.BasePath.get(canvas.getElement()));
  }
}
