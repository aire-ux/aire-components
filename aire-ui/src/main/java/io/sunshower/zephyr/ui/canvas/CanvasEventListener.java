package io.sunshower.zephyr.ui.canvas;

import com.aire.ux.condensation.Attribute;
import com.aire.ux.condensation.Element;
import com.aire.ux.condensation.RootElement;
import com.vaadin.flow.component.DomEvent;
import com.vaadin.flow.component.EventData;
import elemental.json.JsonValue;
import io.sunshower.persistence.id.Identifier;
import io.sunshower.zephyr.ui.aire.AireComponentEvent;
import io.sunshower.zephyr.ui.canvas.CanvasEventListener.CellDefinition;
import io.sunshower.zephyr.ui.canvas.listeners.Location;
import lombok.Getter;
import lombok.Setter;

/** this class is required to translate between CanvasEvents and VertexEvents */
@DomEvent(CanvasEventListener.CATEGORY)
public class CanvasEventListener extends AireComponentEvent<Canvas, CellDefinition> {

  public static final String CATEGORY = "cell-event";

  /**
   * Creates a new event using the given source and indicator whether the event originated from the
   * client side or the server side.
   *
   * @param source the source component
   * @param fromClient <code>true</code> if the event originated from the client
   */
  public CanvasEventListener(
      Canvas source, boolean fromClient, @EventData("event.detail.source") JsonValue target) {
    super(source, fromClient, target);
  }

  @Getter
  @Setter
  @RootElement
  public static final class CellDefinition {

    @Attribute private Cell.Type type;

    @Attribute private Identifier id;

    @Element private Location location;
    @Attribute private String targetEventType;
  }
}
