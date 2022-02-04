package io.sunshower.zephyr.ui.canvas;

import com.aire.ux.condensation.Attribute;
import com.aire.ux.condensation.RootElement;
import com.vaadin.flow.component.DomEvent;
import com.vaadin.flow.component.EventData;
import elemental.json.JsonValue;
import io.sunshower.zephyr.ui.aire.AireComponentEvent;
import io.sunshower.zephyr.ui.canvas.CanvasClickedEvent.Click;
import lombok.Getter;

@DomEvent("canvas-clicked")
public class CanvasClickedEvent extends AireComponentEvent<Canvas, Click> {

  /**
   * Creates a new event using the given source and indicator whether the event originated from the
   * client side or the server side.
   *
   * @param source     the source component
   * @param fromClient <code>true</code> if the event originated from the client
   */
  public CanvasClickedEvent(Canvas source, boolean fromClient,
      @EventData("event.detail.click") JsonValue click
  ) {
    super(source, fromClient, click);
  }

  @Getter
  @RootElement
  public static class Click {

    @Attribute
    private Double x;
    @Attribute
    private Double y;
  }
}
