package io.sunshower.zephyr.ui.canvas;

import static org.junit.jupiter.api.Assertions.*;

import com.aire.ux.condensation.Condensation;
import io.sunshower.persistence.id.Identifiers;
import java.io.IOException;
import lombok.val;
import org.junit.jupiter.api.Test;

class VertexTest {

  @Test
  void ensureWritingVertexWorks() throws IOException {
    val vertex = new Vertex();
    val id = Identifiers.newSequence().next();
    vertex.setId(id);
    val json = Condensation.create("json");
    val result = json.getWriter().write(Vertex.class, vertex);

    val read = json.read(Vertex.class, result);
    assertEquals(id, read.getId());
  }
}
