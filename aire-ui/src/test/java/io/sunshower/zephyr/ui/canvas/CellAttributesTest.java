package io.sunshower.zephyr.ui.canvas;

import static org.junit.jupiter.api.Assertions.assertEquals;

import io.sunshower.arcus.condensation.Condensation;
import io.sunshower.persistence.id.Identifiers;
import java.io.Serializable;
import java.util.HashMap;
import lombok.SneakyThrows;
import lombok.val;
import org.junit.jupiter.api.Test;

class CellAttributesTest {

  @Test
  @SneakyThrows
  void ensureAttributesAreSerializedProperly() {
    val attributes = new CellAttributes();
    val id = Identifiers.newSequence().next();
    val bodyAttribute = new HashMap<String, Serializable>();
    bodyAttribute.put("stroke", "red");
    bodyAttribute.put("strokeWidth", 2);
    attributes.setAttributes(bodyAttribute);
    attributes.setId(id);

    val string = Condensation.write("json", CellAttributes.class, attributes);
    val newValue = Condensation.create("json").read(CellAttributes.class, string);
    assertEquals(id, newValue.getId());
    assertEquals("red", newValue.getAttributes().get("stroke"));
    assertEquals(2d, newValue.getAttributes().get("strokeWidth"));
  }
}
