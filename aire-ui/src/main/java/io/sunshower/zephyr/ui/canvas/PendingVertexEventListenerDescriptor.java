package io.sunshower.zephyr.ui.canvas;

import io.sunshower.zephyr.ui.canvas.listeners.CellListener;
import io.sunshower.zephyr.ui.canvas.listeners.VertexEvent.Type;
import java.util.function.Predicate;
import lombok.Getter;

class PendingVertexEventListenerDescriptor {


  @Getter
  private final Type type;
  @Getter
  private final CellListener<Vertex> listener;
  @Getter
  private final Predicate<Vertex> vertexFilter;

  public PendingVertexEventListenerDescriptor(Type type, CellListener<Vertex> listener,
      Predicate<Vertex> vertexFilter) {
    this.type = type;
    this.listener = listener;
    this.vertexFilter = vertexFilter;
  }
}
