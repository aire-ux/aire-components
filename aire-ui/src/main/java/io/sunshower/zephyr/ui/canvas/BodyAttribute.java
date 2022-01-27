package io.sunshower.zephyr.ui.canvas;

import com.aire.ux.condensation.Alias;
import com.aire.ux.condensation.Attribute;
import com.aire.ux.condensation.RootElement;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@RootElement
@EqualsAndHashCode
public class BodyAttribute implements VertexAttribute {

  private static final String key = "body";

  @Attribute private String stroke;

  @Attribute(alias = @Alias(read = "stroke-width", write = "stroke-width"))
  private Float strokeWidth;

  @Attribute private String fill;

  @Override
  public String getKey() {
    return key;
  }
}
