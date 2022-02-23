package io.sunshower.zephyr.ui.canvas.listeners;

import io.sunshower.zephyr.ui.canvas.Canvas;
import io.sunshower.zephyr.ui.canvas.CanvasEventListener;
import io.sunshower.zephyr.ui.canvas.Cell.Type;
import java.util.NoSuchElementException;
import lombok.NonNull;
import lombok.val;

public interface CanvasEvent {

  Canvas getSource();

  Location getLocation();

  enum CanvasInteractionEventType implements CanvasEventType {
    Clicked("canvas:clicked", "blank:click"),
    ContextMenu("canvas:contextmenu", "blank:contextmenu");

    final int key;
    final String type;
    final String mappedName;

    CanvasInteractionEventType(@NonNull String type, @NonNull String mappedName) {
      this.type = type;
      this.mappedName = mappedName;
      this.key = CanvasEventType.nextKey();
    }

    public static CanvasEventType resolve(String targetEventType) {
      for (val c : values()) {
        if (c.type.equals(targetEventType)) {
          return c;
        }
      }
      throw new NoSuchElementException("No element: " + targetEventType);
    }

    @Override
    public Type getCellType() {
      return Type.None;
    }

    @Override
    public @NonNull String getType() {
      return type;
    }

    @Override
    public @NonNull String getMappedName() {
      return mappedName;
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
