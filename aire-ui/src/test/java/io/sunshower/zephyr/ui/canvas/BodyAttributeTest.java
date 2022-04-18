package io.sunshower.zephyr.ui.canvas;

import io.sunshower.arcus.condensation.Condensation;
import java.io.IOException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class BodyAttributeTest {

  private VertexTemplate template;
  private Condensation condensation;

  @BeforeEach
  void setUp() {
    template = new VertexTemplate();
    condensation = Condensation.create("json");
  }

  @Test
  void ensureNodeAttributeWritesBodyCorrectly() throws IOException {}
}
