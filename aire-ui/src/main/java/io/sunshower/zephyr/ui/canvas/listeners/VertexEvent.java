package io.sunshower.zephyr.ui.canvas.listeners;

import io.sunshower.lang.events.Event;
import io.sunshower.zephyr.ui.canvas.Canvas;
import io.sunshower.zephyr.ui.canvas.CanvasEventListener;
import io.sunshower.zephyr.ui.canvas.Cell.Type;
import io.sunshower.zephyr.ui.canvas.Vertex;
import java.util.NoSuchElementException;
import lombok.Getter;
import lombok.NonNull;
import lombok.val;

public class VertexEvent extends AbstractCellEvent<Vertex> implements Event<Vertex> {

  public VertexEvent(Vertex target, Canvas source, Location location) {
    super(target, source, location);
  }

  public enum EventType implements CanvasEventType {
    Clicked("vertex:clicked", "node:click"),
    MouseEnter("vertex:mouse-enter", "node:mouseenter"),
    MouseLeave("vertex:mouse-leave", "node:mouseleave"),
    ContextMenu("vertex:context-menu", "node:contextmenu");

    private final int key;
    @Getter private final String type;
    @Getter private final String mappedName;

    EventType(@NonNull String type, @NonNull String mappedName) {
      this.type = type;
      this.mappedName = mappedName;
      this.key = CanvasEventType.nextKey();
    }

    public static EventType resolve(@NonNull String type) {
      for (val v : EventType.values()) {
        if (v.type.equals(type)) {
          return v;
        }
      }
      throw new NoSuchElementException("No event-type for " + type);
    }

    @Override
    public Type getCellType() {
      return Type.Vertex;
    }

    @Override
    public int getKey() {
      return key;
    }

    @Override
    public String getCategory() {
      return CanvasEventListener.CATEGORY;
    }
  }
}
