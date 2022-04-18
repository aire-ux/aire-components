package io.sunshower.zephyr.ui.canvas;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import io.sunshower.arcus.condensation.Condensation;
import lombok.SneakyThrows;
import lombok.val;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class VertexTest {

  Condensation condensation;

  @BeforeEach
  void setUp() {
    condensation = Condensation.create("json");
  }

  @Test
  @SneakyThrows
  void ensureVertexIsCopiedCorrectly() {
    val vertex = new Vertex();
    val s = condensation.write(Vertex.class, vertex);
    assertEquals(vertex.getId(), condensation.read(Vertex.class, s).getId());
  }

  @Test
  @SneakyThrows
  void ensurePropertiesAreCopiedCorrectly() {
    val vertex = new Vertex();
    vertex.addProperty("hello", "world");
    val copy = condensation.copy(Vertex.class, vertex);
    assertEquals(vertex.getProperties(), copy.getProperties());
  }

  @Test
  void ensureVertexIdIsSet() {
    assertNotNull(new Vertex().getId());
  }

  @Test
  @SneakyThrows
  void ensureWritingVertexWorks() {
    val vertex = new Vertex();
    val json = Condensation.create("json");
    val result = json.getWriter().write(Vertex.class, vertex);
    val read = json.read(Vertex.class, result);
    assertEquals(read, vertex);
  }
}
