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
import io.sunshower.zephyr.ui.canvas.CanvasCellEventListener.CellDefinition;
import io.sunshower.zephyr.ui.canvas.actions.AddListenerAction;
import io.sunshower.zephyr.ui.canvas.actions.AddListenersAction;
import io.sunshower.zephyr.ui.canvas.listeners.CanvasEvent;
import io.sunshower.zephyr.ui.canvas.listeners.CanvasEventType;
import io.sunshower.zephyr.ui.canvas.listeners.CanvasListener;
import io.sunshower.zephyr.ui.canvas.listeners.CellEvent;
import io.sunshower.zephyr.ui.canvas.listeners.CellListener;
import io.sunshower.zephyr.ui.canvas.listeners.EdgeEvent;
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
  private final AtomicReference<CountdownRegistration> cellEventCountdownRegistration;

  private final List<PendingCanvasEventListenerDescriptor<?, ?>> pendingListeners;

  private Model model;
  private volatile boolean ready;
  private CommandManager commandManager;

  public Canvas() {
    setModel(new SharedGraphModel());
    addOnCanvasReadyListener(this);
    pendingListeners = new ArrayList<>();
    cellEventCountdownRegistration = new AtomicReference<>();
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

  public <T extends Cell, U extends CellEvent<T>> Registration addCellListener(
      CanvasEventType type, CellListener<T, U> listener) {
    return addCellListener(type, listener, e -> true);
  }

  public <T extends Cell, U extends CellEvent<T>> Registration addCellListener(
      CanvasEventType type, CellListener<T, U> listener, Predicate<? super T> filter) {
    if (ready) {
      return registerCellListener(type, listener, filter);
    } else {
      return registerPendingCellListener(type, listener, filter);
    }
  }

  public Registration addCanvasListener(
      CanvasEventType type, CanvasListener<CanvasEvent> listener) {
    if (ready) {
      return registerCanvasListener(type, listener);
    } else {
      return registerPendingCanvasListener(type, listener);
    }
  }

  private Registration registerPendingCanvasListener(
      CanvasEventType type, CanvasListener<CanvasEvent> listener) {

    val delegate = getCanvasEventListener(listener);
    val pending =
        new PendingCanvasEventListenerDescriptor<>(type, type.getCellType(), delegate, this);
    pendingListeners.add(pending);
    return pending;
  }

  public <T> ClientResult<T> invoke(Class<? extends CanvasAction<T>> action, Object... arguments) {
    return ClientMethods.withUiSupplier(this).construct(action, arguments).apply(getModel());
  }

  public Registration addOnCanvasReadyListener(ComponentEventListener<CanvasReadyEvent> listener) {
    return addListener(CanvasReadyEvent.class, listener);
  }

  public Registration addOnCanvasClickedEventListener(
      ComponentEventListener<CanvasClickedEvent> listener) {
    return addListener(CanvasClickedEvent.class, listener);
  }

  /**
   * @return the canvas model for this canvas
   */
  @NonNull
  public final Model getModel() {
    return model;
  }

  @Override
  public void onComponentEvent(CanvasReadyEvent event) {
    ready = true;
    bulkRegisterCellListeners();
  }

  @SuppressWarnings("unchecked")
  public ContextMenu<Vertex> createVertexContextMenu(
      EventType eventType, Predicate<Vertex> filter) {
    val menu = new ContextMenu<Vertex>(this);
    val registration =
        addCellListener(
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
  private <T extends Cell, U extends CellEvent<T>> EventListener<U> getEventListener(
      CellListener<T, U> listener) {
    return (eventType, event) -> listener.on(event.getTarget());
  }

  @NonNull
  private EventListener<CanvasEvent> getCanvasEventListener(CanvasListener<CanvasEvent> listener) {
    return (eventType, event) -> listener.on(event.getTarget());
  }

  private Registration getRegistration() {
    var registration = cellEventCountdownRegistration.get();
    if (registration == null) {
      cellEventCountdownRegistration.set(
          registration =
              new CountdownRegistration(
                  addListener(
                      CanvasCellEventListener.class,
                      event -> {
                        val cellDefinition = event.getValue();
                        val mappedEventType = resolveEventFor(cellDefinition);
//                        val mappedEventType =
//                            VertexEvent.EventType.resolve(cellDefinition.getTargetEventType());
                        model
                            .findCell(cellDefinition.getType(),
                                v -> Objects.equals(cellDefinition.getId(), v.getId()))
                            .ifPresent(
                                v ->
                                    model.dispatchEvent(
                                        mappedEventType::getKey,
                                        () -> createEvent(v, cellDefinition)));
                      })));
    }
    return registration;
  }

  private CanvasEventType resolveEventFor(CellDefinition cellDefinition) {
    switch (cellDefinition.getType()) {
      case Vertex:
        return VertexEvent.EventType.resolve(cellDefinition.getTargetEventType());
      default:
        return EdgeEvent.EventType.resolve(cellDefinition.getTargetEventType());
    }
  }

  @SuppressWarnings("unchecked")
  private <T extends Cell> CellEvent<T> createEvent(T source, CellDefinition definition) {
    switch (source.getType()) {
      case Vertex:
        return (CellEvent<T>) new VertexEvent((Vertex) source, this, definition.getLocation());
      default:
        return (CellEvent<T>) new EdgeEvent((Edge) source, this, definition.getLocation());
    }
  }

  private void bulkRegisterCellListeners() {
    val definitions = new ArrayList<ListenerDefinition>(pendingListeners.size());
    for (val pendingListener : pendingListeners) {
      val type = pendingListener.getType();
      val cellType = pendingListener.getCellType();
      val definition =
          new ListenerDefinition(
              SharedGraphModel.identifierSequence.next(),
              type.getMappedName(),
              cellType,
              CanvasCellEventListener.CATEGORY,
              type.getType());
      definitions.add(definition);
      model.addEventListener(pendingListener.getDelegate(), type::getKey);
      val registration = getRegistration();
      pendingListener.setRegistration(registration);
    }
    invoke(AddListenersAction.class, definitions);
  }

  private <T extends Cell, U extends CellEvent<T>> Registration registerPendingCellListener(
      CanvasEventType type, CellListener<T, U> listener, Predicate<? super T> filter) {
    val delegate = getEventListener(listener);
    val pending =
        new PendingCanvasEventListenerDescriptor<>(type, type.getCellType(), delegate, this);
    pendingListeners.add(pending);
    return pending;
  }

  private <T extends Cell, U extends CellEvent<T>> Registration registerCellListener(
      CanvasEventType type, CellListener<T, U> listener, Predicate<? super T> filter) {

    val definition =
        new ListenerDefinition(
            SharedGraphModel.identifierSequence.next(),
            type.getMappedName(),
            type.getCellType(),
            type.getCategory(),
            type.getType());
    val delegate = getEventListener(listener);
    invoke(AddListenerAction.class, definition);
    model.addEventListener(delegate, type::getKey);

    val registration = getRegistration();
    return () -> {
      model.removeEventListener(delegate);
      registration.remove();
    };
  }

  private Registration registerCanvasListener(
      CanvasEventType type, CanvasListener<CanvasEvent> listener) {
    val definition =
        new ListenerDefinition(
            SharedGraphModel.identifierSequence.next(),
            type.getMappedName(),
            type.getCellType(),
            type.getCategory(),
            type.getType());
    val delegate = getCanvasEventListener(listener);
    invoke(AddListenerAction.class, definition);
    model.addEventListener(delegate, type::getKey);
    val registration = getRegistration();
    return () -> {
      model.removeEventListener(delegate);
      registration.remove();
    };
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
