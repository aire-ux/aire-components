package io.sunshower.zephyr.ui.canvas;

import com.aire.ux.condensation.Attribute;
import com.aire.ux.condensation.RootElement;
import io.sunshower.zephyr.ui.canvas.attributes.VertexTemplateBuilder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@RootElement
public class VertexTemplate extends AbstractCellTemplate {

  /** the name of this descriptor */
  @Attribute private String name;

  @Attribute private String inherit;
  /** the width of nodes struck from this template */
  @Attribute private double width;

  /** the height of nodes struck from this template */
  @Attribute private double height;

  public VertexTemplate() {}

  public static VertexTemplateBuilder newBuilder() {
    return new VertexTemplateBuilder();
  }

  public static VertexTemplateBuilder newBuilder(String name) {
    return new VertexTemplateBuilder().name(name);
  }
}
