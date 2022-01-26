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
    template.setHeight(100f);
    template.setWidth(100f);
    val body = new BodyAttribute();
    body.setStroke("#5F95FF");
    body.setStrokeWidth(1f);
    body.setFill("rgba(95,149,255,0.05)");

    template.addAttribute(body);
    System.out.println(condensation.getWriter().write(VertexTemplate.class, template));
  }

}