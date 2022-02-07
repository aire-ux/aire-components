package io.sunshower.zephyr.ui.canvas;

import com.aire.ux.condensation.Attribute;
import com.aire.ux.condensation.Element;
import com.aire.ux.condensation.RootElement;
import io.sunshower.persistence.id.Identifier;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;

@RootElement
public class Edge extends AbstractCell {

  @Getter
  @Setter
  @Attribute
  private Identifier target;

  @Getter
  @Setter
  @Attribute
  private Identifier source;

  @Element
  private EdgeTemplate template;


  public Edge() {
    this(SharedGraphModel.identifierSequence.next());
  }

  public Edge(Identifier identifier) {
    super(Type.Edge, identifier);
  }

  public Edge(@NonNull Identifier source, @NonNull Identifier target) {
    this();
    setSource(source);
    setTarget(target);
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
