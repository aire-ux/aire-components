package io.sunshower.zephyr.ui.canvas;

import com.vaadin.flow.component.AttachEvent;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.DetachEvent;
import com.vaadin.flow.component.HtmlContainer;
import com.vaadin.flow.component.Tag;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.dependency.JsModule;
import com.vaadin.flow.component.dependency.NpmPackage;
import com.vaadin.flow.shared.Registration;
import io.sunshower.lang.events.EventListener;
import io.sunshower.zephyr.ui.canvas.actions.AddListenerAction;
import io.sunshower.zephyr.ui.canvas.actions.AddListenersAction;
import io.sunshower.zephyr.ui.canvas.listeners.CellListener;
import io.sunshower.zephyr.ui.canvas.listeners.ListenerDefinition;
import io.sunshower.zephyr.ui.canvas.listeners.VertexEvent;
import io.sunshower.zephyr.ui.canvas.listeners.VertexEvent.EventType;
import io.sunshower.zephyr.ui.components.ContextMenu;
import io.sunshower.zephyr.ui.components.ContextMenuEvent;
import io.sunshower.zephyr.ui.components.ContextMenuEvent.Type;
import io.sunshower.zephyr.ui.rmi.ClientMethods;
import io.sunshower.zephyr.ui.rmi.ClientResult;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Predicate;
import lombok.NonNull;
import lombok.val;

@Tag("aire-canvas")
@JsModule("@antv/x6/dist/x6.js")
@JsModule("@antv/layout/es/index.js")
@CssImport("@antv/x6/dist/x6.css")
@JsModule("./aire/ui/canvas/cell.ts")
@JsModule("./aire/ui/canvas/canvas.ts")
@JsModule("@aire-ux/aire-condensation/dist/index.js")
@CssImport("./styles/aire/ui/canvas/canvas.css")
@NpmPackage(value = "@antv/x6", version = "1.30.0")
@NpmPackage(value = "@antv/layout", version = "0.1.31")
@NpmPackage(value = "@aire-ux/aire-condensation", version = "0.1.5")
public class Canvas extends HtmlContainer implements ComponentEventListener<CanvasReadyEvent> {

  /**
   * registering multiple CanvasVertexEventListeners causes an explosion in the number of cellevents
   * dispatched. This will prevent that
   */
  private final AtomicReference<CountdownRegistration> vertexEventCountdownRegistration;

  private final List<PendingVertexEventListenerDescriptor> pendingVertexEventListeners;

  private Model model;
  private volatile boolean ready;
  private CommandManager commandManager;

  public Canvas() {
    setModel(new SharedGraphModel());
    addOnCanvasReadyListener(this);
    pendingVertexEventListeners = new ArrayList<>();
    vertexEventCountdownRegistration = new AtomicReference<>();
  }

  public Model setModel(@NonNull final Model model) {
    val m = this.model;
    this.model = model;
    model.setHost(this);
    this.commandManager = model.getCommandManager();
    return m;
  }

  @Override
  protected void onAttach(AttachEvent attachEvent) {
    super.onAttach(attachEvent);
    model.attach(this);
  }

  @Override
  protected void onDetach(DetachEvent detachEvent) {
    super.onDetach(detachEvent);
    model.detach(this);
  }

  public Registration addVertexListener(
      EventType type, CellListener<Vertex, VertexEvent> listener) {
    return addVertexListener(type, listener, v -> true);
  }

  public Registration addVertexListener(
      EventType type, CellListener<Vertex, VertexEvent> listener, Predicate<Vertex> vertexFilter) {

    if (!ready) {
      return registerPendingVertexListener(type, listener, vertexFilter);
    } else {
      return doRegisterVertexListener(type, listener, vertexFilter);
    }
  }

  private Registration registerPendingVertexListener(
      EventType type, CellListener<Vertex, VertexEvent> listener, Predicate<Vertex> vertexFilter) {
    val delegate = getEventListener(listener);
    val pending = new PendingVertexEventListenerDescriptor(type, delegate, this);
    pendingVertexEventListeners.add(pending);
    return pending;
  }

  public <T> ClientResult<T> invoke(Class<? extends Action<T>> action, Object... arguments) {
    return ClientMethods.withUiSupplier(this).construct(action, arguments).apply(getModel());
  }

  public Registration addOnCanvasReadyListener(ComponentEventListener<CanvasReadyEvent> listener) {
    return addListener(CanvasReadyEvent.class, listener);
  }

  public Registration addOnCanvasClickedEventListener(
      ComponentEventListener<CanvasClickedEvent> listener) {
    return addListener(CanvasClickedEvent.class, listener);
  }

  /** @return the canvas model for this canvas */
  @NonNull
  public final Model getModel() {
    return model;
  }

  @Override
  public void onComponentEvent(CanvasReadyEvent event) {
    ready = true;
    bulkRegisterPendingVertexListeners();
  }

  @SuppressWarnings("unchecked")
  public ContextMenu<Vertex> createVertexContextMenu(
      EventType eventType, Predicate<Vertex> filter) {
    val menu = new ContextMenu<Vertex>(this);
    val registration =
        addVertexListener(
            eventType,
            vertex -> {
              menu.open(
                  new ContextMenuEvent<>(vertex.getTarget(), Type.Opened, vertex.getLocation()));
            },
            filter);
    return menu;
  }

  @SuppressWarnings("unchecked")
  public ContextMenu<Vertex> createVertexContextMenu(EventType eventType) {
    return createVertexContextMenu(eventType, vertex -> true);
  }

  @NonNull
  private EventListener<VertexEvent> getEventListener(CellListener<Vertex, VertexEvent> listener) {
    return (eventType, event) -> listener.on(event.getTarget());
  }

  private Registration doRegisterVertexListener(
      EventType type, CellListener<Vertex, VertexEvent> listener, Predicate<Vertex> vertexFilter) {
    val definition =
        new ListenerDefinition(
            SharedGraphModel.identifierSequence.next(),
            type.getMappedName(),
            CanvasVertexEventListener.CATEGORY,
            type.getType());
    val delegate = getEventListener(listener);
    invoke(AddListenerAction.class, definition);
    model.addEventListener(delegate, type::ordinal);

    val registration = getRegistration();
    return () -> {
      model.removeEventListener(delegate);
      registration.remove();
    };
  }

  private Registration getRegistration() {
    var registration = vertexEventCountdownRegistration.get();
    if (registration == null) {
      vertexEventCountdownRegistration.set(
          registration =
              new CountdownRegistration(
                  addListener(
                      CanvasVertexEventListener.class,
                      event -> {
                        val vertexDefinition = event.getValue();
                        val mappedEventType =
                            VertexEvent.EventType.resolve(vertexDefinition.getTargetEventType());
                        model
                            .findVertex(v -> Objects.equals(vertexDefinition.getId(), v.getId()))
                            .ifPresent(
                                v ->
                                    model.dispatchEvent(
                                        mappedEventType::ordinal,
                                        () ->
                                            (new VertexEvent(
                                                v, this, vertexDefinition.getLocation()))));
                      })));
    }
    return registration;
  }

  private void bulkRegisterPendingVertexListeners() {

    val definitions = new ArrayList<ListenerDefinition>(pendingVertexEventListeners.size());
    for (val pendingVertexListener : pendingVertexEventListeners) {
      val type = pendingVertexListener.getType();
      val definition =
          new ListenerDefinition(
              SharedGraphModel.identifierSequence.next(),
              type.getMappedName(),
              CanvasVertexEventListener.CATEGORY,
              type.getType());
      definitions.add(definition);
      model.addEventListener(pendingVertexListener.getDelegate(), type::ordinal);
      val registration = getRegistration();
      pendingVertexListener.setRegistration(registration);
    }
    invoke(AddListenersAction.class, definitions);
  }

  private class CountdownRegistration implements Registration {

    private final Registration delegate;

    private CountdownRegistration(Registration delegate) {
      this.delegate = delegate;
    }

    @Override
    public void remove() {
      if (model != null && model.getListenerCount() == 0) {
        delegate.remove();
      }
    }
  }
}
