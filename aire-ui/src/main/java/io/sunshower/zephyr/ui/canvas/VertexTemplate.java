package io.sunshower.zephyr.ui.canvas;

import com.aire.ux.condensation.Alias;
import com.aire.ux.condensation.Attribute;
import com.aire.ux.condensation.Element;
import com.aire.ux.condensation.RootElement;
import io.sunshower.zephyr.ui.canvas.attributes.VertexTemplateBuilder;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@RootElement
public class VertexTemplate {

  /**
   * the name of this descriptor
   */
  @Attribute
  private String name;

  /**
   * the width of nodes struck from this template
   */
  @Attribute
  private float width;

  /**
   * the height of nodes struck from this template
   */
  @Attribute
  private float height;

  @Element(alias = @Alias(read = "attrs", write = "attrs"))
  private Map<String, Map<String, Serializable>> attributes;

  @Element(alias = @Alias(read = "markup", write = "markup"))
  private List<Selector> selectors;

  public VertexTemplate() {
    selectors = new ArrayList<>();
  }

  public static VertexTemplateBuilder newBuilder() {
    return new VertexTemplateBuilder();
  }


  public static VertexTemplateBuilder newBuilder(String name) {
    return new VertexTemplateBuilder().name(name);
  }
  public void addSelector(String tagName, String selector) {
    selectors.add(new Selector(tagName, selector));
  }
}
