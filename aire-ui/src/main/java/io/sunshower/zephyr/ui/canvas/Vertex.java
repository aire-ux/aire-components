package io.sunshower.zephyr.ui.canvas;

import com.aire.ux.condensation.Attribute;
import com.aire.ux.condensation.RootElement;
import io.sunshower.persistence.id.Identifier;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@RootElement
@EqualsAndHashCode
public class Vertex {

  /** the x location of this vertex */
  @Getter @Setter @Attribute private Float x;

  /** the y location of this vertex */
  @Getter @Setter @Attribute private Float y;

  /** the label of this vertex */
  @Getter @Setter @Attribute private String label;

  /** the width of this vertex */
  @Getter @Setter @Attribute private Float width;

  /** the height of this vertex */
  @Getter @Setter @Attribute private Float height;

  /** the ID of this vertex */
  @Getter @Setter @Attribute private Identifier id;

  /** */
  @Getter @Setter @Attribute private String shape;

  private VertexTemplate template;

  public Vertex() {}

  public void setTemplate(@NonNull VertexTemplate template) {
    this.shape = template.getName();
    this.template = template;
  }
}
