package io.sunshower.zephyr.ui.canvas;

import com.vaadin.flow.shared.Registration;
import io.sunshower.lang.events.EventListener;
import io.sunshower.zephyr.ui.canvas.CanvasVertexEventListener.VertexDefinition;
import io.sunshower.zephyr.ui.canvas.listeners.VertexEvent.Type;
import lombok.Getter;
import lombok.NonNull;

class PendingVertexEventListenerDescriptor implements Registration {

  @Getter private final Type type;
  private final Canvas canvas;
  @Getter private final EventListener<VertexDefinition> delegate;

  private Registration registration;

  public PendingVertexEventListenerDescriptor(
      @NonNull Type type,
      @NonNull EventListener<VertexDefinition> delegate,
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
