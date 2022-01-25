package io.sunshower.zephyr.ui.canvas;

public interface Cell {

  Type getType();

  enum Type {
    Edge,
    Vertex
  }
}
