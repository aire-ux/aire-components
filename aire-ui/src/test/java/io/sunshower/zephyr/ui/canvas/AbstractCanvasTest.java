package io.sunshower.zephyr.ui.canvas;

import com.aire.ux.condensation.Condensation;
import com.aire.ux.condensation.DocumentWriter;
import io.sunshower.zephyr.condensation.CondensationUtilities;
import org.junit.jupiter.api.BeforeEach;

public class AbstractCanvasTest {

  protected DocumentWriter writer;
  protected Condensation condensation;
  protected VertexTemplate template;

  @BeforeEach
  void setUp() {
    condensation = Condensation.create("json");
    writer = condensation.getWriter();
    template =
        CondensationUtilities.read(
            VertexTemplate.class,
            "classpath:canvas/resources/nodes/templates/module-node-template.json");
  }
}
