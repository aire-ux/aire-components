package io.sunshower.zephyr.ui.canvas;

import com.vaadin.flow.shared.Registration;
import io.sunshower.lang.events.EventListener;
import io.sunshower.zephyr.ui.canvas.listeners.CanvasEvent;
import io.sunshower.zephyr.ui.canvas.listeners.CanvasEventType;
import lombok.Getter;
import lombok.NonNull;

class PendingCanvasEventListenerDescriptor<T extends CanvasEvent, U extends CanvasEventType>
    implements Registration {

  private final Canvas canvas;
  @Getter private final Cell.Type cellType;
  @Getter private final CanvasEventType type;

  @Getter private final EventListener<T> delegate;

  private Registration registration;

  public PendingCanvasEventListenerDescriptor(
      @NonNull U type,
      @NonNull Cell.Type cellType,
      @NonNull EventListener<T> delegate,
      @NonNull Canvas canvas) {
    this.type = type;
    this.canvas = canvas;
    this.delegate = delegate;
    this.cellType = cellType;
  }

  @Override
  public void remove() {
    canvas.getModel().removeEventListener(delegate);
    if (registration != null) {
      registration.remove();
    }
  }

  public void setRegistration(@NonNull Registration registration) {
    this.registration = registration;
  }
}
