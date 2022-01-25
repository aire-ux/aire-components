package io.sunshower.zephyr.ui.canvas;

import com.aire.ux.condensation.Attribute;
import com.aire.ux.condensation.RootElement;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@RootElement
public class Vertex {

  @Attribute private Float x;

  @Attribute private Float y;

  @Attribute private String label;
  @Attribute private Float width;
  @Attribute private Float height;
}
