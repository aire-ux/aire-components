package io.sunshower.zephyr.ui.canvas;

import com.aire.ux.condensation.Condensation;
import java.io.IOException;
import lombok.val;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class VertexTemplateTest {

  private Condensation condensation;

  @BeforeEach
  void setUp() {
    condensation = Condensation.create("json");
  }

  @Test
  void ensureBuilderApiWorks() throws IOException {
    val result = VertexTemplate.newBuilder("test")
        .width(100f)
        .height(100f)
        .attribute("body")
        .property("stroke")
        .hex("5F95FF")
        .property("strokeWidth")
        .number(1)
        .property("fill")
        .string("rgba(95,149,255,0.05)")
        .tagName("rect")
        .selector("body")
        .attribute("image")
        .property("xlink:href")
        .string("https://gw.alipayobjects.com/zos/antfincdn/FLrTNDvlna/antv.png")
        .tagName("image").selector("image")
        .create();

    System.out.println(condensation
        .getWriter().write(VertexTemplate.class, result));
  }

}