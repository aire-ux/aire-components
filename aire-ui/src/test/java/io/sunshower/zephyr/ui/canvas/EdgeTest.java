package io.sunshower.zephyr.ui.canvas;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import com.aire.ux.condensation.Condensation;
import io.sunshower.persistence.id.Identifier;
import io.sunshower.persistence.id.Identifiers;
import io.sunshower.zephyr.condensation.CondensationUtilities;
import lombok.SneakyThrows;
import lombok.val;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class EdgeTest {

  private EdgeTemplate result;
  private Condensation condensation;

  @BeforeEach
  void setUp() {
    condensation = Condensation.create("json");
    result =
        CondensationUtilities.read(
            EdgeTemplate.class,
            "classpath:canvas/resources/nodes/templates/module-edge-template.json");
  }

  @Test
  void ensureEdgeConnectorIsConstructedProperly() {
    val connector = result.getConnector();
    assertNotNull(connector);
    assertEquals("smooth", connector.getName());
  }

  @Test
  void ensureEdgeAttributesExist() {
    val attributes = result.getAttributes();
    val line = attributes.get("line");
    assertNotNull(line);

    assertEquals(1d, line.get("strokeWidth"));
  }

  @Test
  @SneakyThrows
  void ensureEdgeIsSerializedCorrectly() {
    val edge = new Edge();
    val seq = Identifiers.newSequence();
    val source = seq.next();
    val target = seq.next();
    edge.setCellTemplate(result);
    edge.setSource(source);
    edge.setTarget(target);
    val copy = condensation.copy(Edge.class, edge);
    assertNotNull(copy.getCellTemplate());
    assertEquals(source, copy.getSource());
    assertEquals("smooth", copy.getCellTemplate().getConnector().getName());
    assertEquals(1d, copy.getCellTemplate().getAttributes().get("line").get("strokeWidth"));
  }


}