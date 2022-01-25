package io.sunshower.zephyr.ui.canvas;

public interface Element {

  Type getType();

  enum Type {
    Edge,
    Vertex
  }
}
