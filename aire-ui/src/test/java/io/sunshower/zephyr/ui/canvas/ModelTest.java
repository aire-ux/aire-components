package io.sunshower.zephyr.ui.canvas;

import static org.junit.jupiter.api.Assertions.*;

import lombok.val;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ModelTest {

  private Model model;

  @BeforeEach
  void setUp() {
    model = new SharedGraphModel();
  }

  @Test
  void ensureIdIsSet() {
    assertNotNull(new Vertex().getId());
  }

  @Test
  void ensureConnectingEdgesResultsInConnectedEdges() {
    val source = new Vertex();
    val target = new Vertex();
    val edge = model.connect(source, target);
    assertEquals(source.getId(), edge.getSource());
    assertEquals(target.getId(), edge.getTarget());
  }
}
