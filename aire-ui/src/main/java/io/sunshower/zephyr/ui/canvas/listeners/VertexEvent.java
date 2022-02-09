package io.sunshower.zephyr.ui.canvas.listeners;

import io.sunshower.lang.events.Event;
import io.sunshower.zephyr.ui.canvas.Canvas;
import io.sunshower.zephyr.ui.canvas.Vertex;
import java.util.NoSuchElementException;
import lombok.Getter;
import lombok.NonNull;
import lombok.val;

public class VertexEvent extends AbstractCellEvent<Vertex> implements Event<Vertex> {

  public VertexEvent(Vertex target, Canvas source, Location location) {
    super(target, source, location);
  }

  public enum EventType {
    Clicked("vertex:clicked", "node:click"),
    ContextMenu("vertex:context-menu", "node:contextmenu");

    @Getter
    final String type;
    @Getter
    final String mappedName;

    EventType(@NonNull String type, @NonNull String mappedName) {
      this.type = type;
      this.mappedName = mappedName;
    }

    public static EventType resolve(@NonNull String type) {
      for (val v : EventType.values()) {
        if (v.type.equals(type)) {
          return v;
        }
      }
      throw new NoSuchElementException("No event-type for " + type);
    }
  }
}
