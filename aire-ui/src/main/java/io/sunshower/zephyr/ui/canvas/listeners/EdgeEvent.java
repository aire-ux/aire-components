package io.sunshower.zephyr.ui.canvas.listeners;

import io.sunshower.lang.events.Event;
import io.sunshower.zephyr.ui.canvas.Canvas;
import io.sunshower.zephyr.ui.canvas.CanvasCellEventListener;
import io.sunshower.zephyr.ui.canvas.Cell.Type;
import io.sunshower.zephyr.ui.canvas.Edge;
import java.util.NoSuchElementException;
import lombok.Getter;
import lombok.NonNull;
import lombok.val;

public class EdgeEvent extends AbstractCellEvent<Edge> implements Event<Edge> {

  public EdgeEvent(Edge target, Canvas source, Location location) {
    super(target, source, location);
  }

  public enum EventType implements CanvasEventType {
    Clicked("edge:clicked", "edge:click"),
    ContextMenu("edge:context-menu", "edge:contextmenu");

    final int key;
    @Getter private final String type;
    @Getter private final String mappedName;

    EventType(@NonNull String type, @NonNull String mappedName) {
      this.type = type;
      this.mappedName = mappedName;
      this.key = CanvasEventType.nextKey();
    }

    public static EdgeEvent.EventType resolve(@NonNull String type) {
      for (val v : EdgeEvent.EventType.values()) {
        if (v.type.equals(type)) {
          return v;
        }
      }
      throw new NoSuchElementException("No event-type for " + type);
    }

    @Override
    public Type getCellType() {
      return Type.Edge;
    }

    @Override
    public int getKey() {
      return key;
    }

    @Override
    public String getCategory() {
      return CanvasCellEventListener.CATEGORY;
    }
  }
}
