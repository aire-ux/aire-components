package io.sunshower.zephyr.ui.canvas;

import com.aire.ux.condensation.Alias;
import com.aire.ux.condensation.Attribute;
import com.aire.ux.condensation.Element;
import com.aire.ux.condensation.RootElement;
import java.util.HashMap;
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


  /**
   * the attributes of this template
   */
  @Element(alias = @Alias(write = "attrs", read = "attrs"))
  private Map<String, VertexAttribute> attributes;

  public VertexTemplate() {
    this.attributes = new HashMap<>();
  }

  public void addAttribute(VertexAttribute attribute) {
    this.attributes.put(attribute.getKey(), attribute);
  }
}
