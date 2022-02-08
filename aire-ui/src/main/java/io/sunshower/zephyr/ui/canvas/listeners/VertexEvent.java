package io.sunshower.zephyr.ui.canvas.listeners;

import io.sunshower.zephyr.ui.canvas.Vertex;
import lombok.Getter;
import lombok.NonNull;

public class VertexEvent implements CellEvent<Vertex> {

  public enum Type {
    Clicked("vertex:clicked", "node:click");
    @Getter final String type;
    @Getter final String mappedName;

    Type(@NonNull String type, @NonNull String mappedName) {
      this.type = type;
      this.mappedName = mappedName;
    }
  }
}
