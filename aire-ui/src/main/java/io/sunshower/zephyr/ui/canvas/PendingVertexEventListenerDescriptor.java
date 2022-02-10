package io.sunshower.zephyr.ui.canvas;

import com.vaadin.flow.shared.Registration;
import io.sunshower.lang.events.EventListener;
import io.sunshower.zephyr.ui.canvas.listeners.VertexEvent;
import io.sunshower.zephyr.ui.canvas.listeners.VertexEvent.EventType;
import lombok.Getter;
import lombok.NonNull;

class PendingVertexEventListenerDescriptor implements Registration {

  @Getter private final EventType type;
  private final Canvas canvas;
  @Getter private final EventListener<VertexEvent> delegate;

  private Registration registration;

  public PendingVertexEventListenerDescriptor(
      @NonNull VertexEvent.EventType type,
      @NonNull EventListener<VertexEvent> delegate,
      @NonNull Canvas canvas) {
    this.type = type;
    this.canvas = canvas;
    this.delegate = delegate;
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
