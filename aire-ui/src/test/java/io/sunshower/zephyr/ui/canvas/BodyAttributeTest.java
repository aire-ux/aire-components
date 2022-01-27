package io.sunshower.zephyr.ui.canvas;

import com.aire.ux.condensation.Condensation;
import java.io.IOException;
import lombok.val;
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
  void ensureNodeAttributeWritesBodyCorrectly() throws IOException {
  }
}
