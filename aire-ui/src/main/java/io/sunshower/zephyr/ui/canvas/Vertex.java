package io.sunshower.zephyr.ui.canvas;

import io.sunshower.arcus.condensation.Attribute;
import io.sunshower.arcus.condensation.Element;
import io.sunshower.arcus.condensation.RootElement;
import io.sunshower.persistence.id.Identifier;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@RootElement
public class Vertex extends AbstractCell {

  /** the x location of this vertex */
  @Getter @Setter @Attribute private Double x;

  /** the y location of this vertex */
  @Getter @Setter @Attribute private Double y;

  /** the label of this vertex */
  @Getter @Setter @Attribute private String label;

  /** the width of this vertex */
  @Getter @Setter @Attribute private Double width;

  /** the height of this vertex */
  @Getter @Setter @Attribute private Double height;

  /** */
  @Getter @Setter @Attribute private String shape;

  @Element private VertexTemplate template;

  public Vertex() {
    this(SharedGraphModel.identifierSequence.next());
  }

  public Vertex(Identifier id) {
    super(Type.Vertex, id);
  }

  public void setTemplate(@NonNull VertexTemplate template) {
    this.shape = template.getName();
    this.template = template;
  }

  @Override
  public CellTemplate getCellTemplate() {
    return template;
  }

  @Override
  public void setCellTemplate(CellTemplate template) {
    this.template = (VertexTemplate) template;
  }
}
