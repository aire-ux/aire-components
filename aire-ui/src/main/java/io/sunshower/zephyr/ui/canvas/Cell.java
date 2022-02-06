package io.sunshower.zephyr.ui.canvas;

import io.sunshower.persistence.id.Identifier;

public interface Cell {

  Type getType();

  Identifier getId();

  CellTemplate getCellTemplate();

  void setCellTemplate(CellTemplate template);

  enum Type {
    Edge,
    Vertex
  }
}
