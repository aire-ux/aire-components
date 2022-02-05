package io.sunshower.zephyr.ui.canvas;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.aire.ux.condensation.Condensation;
import io.sunshower.zephyr.condensation.CondensationUtilities;
import java.util.ArrayList;
import java.util.List;
import lombok.SneakyThrows;
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
  void ensureReadingResourceWorks() {
    val result =
        CondensationUtilities.read(
            VertexTemplate.class,
            "classpath:canvas/resources/nodes/templates/module-node-template.json");
    assertEquals("module", result.getName());
  }

  @Test
  void ensureReadingAttributesWorks() {
    val result =
        CondensationUtilities.read(
            VertexTemplate.class,
            "classpath:canvas/resources/nodes/templates/module-node-template.json");
    assertEquals(List.of("body", "image"), new ArrayList<>(result.getAttributes().keySet()));
  }

  @Test
  void ensureBuilderApiWorks() {
    val result =
        VertexTemplate.newBuilder("test")
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
            .tagName("image")
            .selector("image")
            .create();
    assertEquals(2, result.getAttributes().size());
    assertEquals(2, result.getSelectors().size());
  }

  @Test
  @SneakyThrows
  void ensureTemplateIsReadableFromJson() {
    val document =
        """
            {
                "width": 200,
                "height": 60,
                "attrs": {
                  "body": {
                    "stroke": "#5F95FF",
                    "strokeWidth": 1,
                    "fill": "rgba(95,149,255,0.05)"
                  },
                  "image": {
                    "xlink:href":
                      "https://gw.alipayobjects.com/zos/antfincdn/FLrTNDvlna/antv.png",
                    "width": 16,
                    "height": 16,
                    "x": 12,
                    "y": 12
                  },
                  "title": {
                    "text": "Node",
                    "refX": 40,
                    "refY": 14,
                    "fill": "rgba(0,0,0,0.85)",
                    "fontSize": 12,
                    "text-anchor": "start"
                  },
                  "text": {
                    "text": "this is content text",
                    "refX": 40,
                    "refY": 38,
                    "fontSize": 12,
                    "fill": "rgba(0,0,0,0.6)",
                    "text-anchor": "start"
                  }
                },
                "markup": [
                  {
                    "tagName": "rect",
                    "selector": "body"
                  },
                  {
                    "tagName": "image",
                    "selector": "image"
                  },
                  {
                    "tagName": "text",
                    "selector": "title"
                  },
                  {
                    "tagName": "text",
                    "selector": "text"
                  }
                ]
              }
                    """;
    val template = condensation.read(VertexTemplate.class, document);
    assertEquals(4, template.getAttributes().size());
    assertEquals(6, template.getAttributes().get("text").size(), 6);
  }

  @Test
  void ensureCollectionIsReadableFromList() {
    val document =
        """
            [{
                "width": 200,
                "height": 60,
                "attrs": {
                  "body": {
                    "stroke": "#5F95FF",
                    "strokeWidth": 1,
                    "fill": "rgba(95,149,255,0.05)"
                  },
                  "image": {
                    "xlink:href":
                      "https://gw.alipayobjects.com/zos/antfincdn/FLrTNDvlna/antv.png",
                    "width": 16,
                    "height": 16,
                    "x": 12,
                    "y": 12
                  },
                  "title": {
                    "text": "Node",
                    "refX": 40,
                    "refY": 14,
                    "fill": "rgba(0,0,0,0.85)",
                    "fontSize": 12,
                    "text-anchor": "start"
                  },
                  "text": {
                    "text": "this is content text",
                    "refX": 40,
                    "refY": 38,
                    "fontSize": 12,
                    "fill": "rgba(0,0,0,0.6)",
                    "text-anchor": "start"
                  }
                },
                "markup": [
                  {
                    "tagName": "rect",
                    "selector": "body"
                  },
                  {
                    "tagName": "image",
                    "selector": "image"
                  },
                  {
                    "tagName": "text",
                    "selector": "title"
                  },
                  {
                    "tagName": "text",
                    "selector": "text"
                  }
                ]
              }]
                    """;
    val template = condensation.read(VertexTemplate.class, document);
    assertEquals(4, template.getAttributes().size());
    assertEquals(6, template.getAttributes().get("text").size(), 6);
  }
}
