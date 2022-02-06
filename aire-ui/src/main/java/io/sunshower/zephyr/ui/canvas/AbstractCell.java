package io.sunshower.zephyr.ui.canvas;

import com.aire.ux.condensation.Element;
import com.aire.ux.condensation.RootElement;
import io.sunshower.arcus.incant.LazyPropertyAware;
import io.sunshower.persistence.id.Identifier;
import lombok.EqualsAndHashCode;
import lombok.NonNull;
import lombok.ToString;

@ToString
@RootElement
@EqualsAndHashCode
public class AbstractCell extends LazyPropertyAware implements Cell {

  @NonNull private final Type type;
  @NonNull private final Identifier identifier;

  @Element private CellTemplate template;

  protected AbstractCell(final Type type, final @NonNull Identifier identifier) {
    this.type = type;
    this.identifier = identifier;
  }

  @Override
  public Type getType() {
    return type;
  }

  @Override
  public Identifier getId() {
    return identifier;
  }

  @Override
  public CellTemplate getCellTemplate() {
    return template;
  }

  @Override
  public void setCellTemplate(CellTemplate template) {
    this.template = template;
  }
}
