package io.sunshower.zephyr.ui.canvas;

import io.sunshower.arcus.condensation.Attribute;
import io.sunshower.arcus.condensation.Element;
import io.sunshower.arcus.condensation.RootElement;
import io.sunshower.persistence.id.Identifier;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;

@RootElement
public class Edge extends AbstractCell {

  @Getter @Setter @Attribute private Identifier target;

  @Getter @Setter @Attribute private Identifier source;

  @Element private EdgeTemplate template;

  public Edge() {
    this(SharedGraphModel.identifierSequence.next());
  }

  public Edge(Identifier identifier) {
    super(Type.Edge, identifier);
  }

  public Edge(@NonNull Identifier source, @NonNull Identifier target) {
    this(source, target, (EdgeTemplate) null);
  }

  public Edge(Identifier id, Identifier source, Identifier target) {
    this(id);
    this.source = source;
    this.target = target;
  }

  public Edge(Identifier source, Identifier target, EdgeTemplate template) {
    this();
    setSource(source);
    setTarget(target);
    setCellTemplate(template);
  }

  @Override
  public EdgeTemplate getCellTemplate() {
    return template;
  }

  @Override
  public void setCellTemplate(CellTemplate template) {
    this.template = (EdgeTemplate) template;
  }
}
