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
import io.sunshower.zephyr.ui.canvas.CanvasEventListener.CellDefinition;
import io.sunshower.zephyr.ui.canvas.actions.AddListenerAction;
import io.sunshower.zephyr.ui.canvas.actions.AddListenersAction;
import io.sunshower.zephyr.ui.canvas.listeners.CanvasEvent;
import io.sunshower.zephyr.ui.canvas.listeners.CanvasEvent.CanvasInteractionEventType;
import io.sunshower.zephyr.ui.canvas.listeners.CanvasEventType;
import io.sunshower.zephyr.ui.canvas.listeners.CanvasInteractionEvent;
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

  /**
   * @param model the model to set. Retargets the model's host to this
   * @return the model
   */
  public Model setModel(@NonNull final Model model) {
    val m = this.model;
    this.model = model;
    model.setHost(this);
    this.commandManager = model.getCommandManager();
    return m;
  }

  /**
   * @param type the type of the event to listen for
   * @param listener the listener
   * @param <T> the type of cell
   * @param <U> the type of cell event
   * @return a registration that will dispose of the listener
   */
  public <T extends Cell, U extends CellEvent<T>> Registration addCellListener(
      CanvasEventType type, CellListener<T, U> listener) {
    return addCellListener(type, listener, e -> true);
  }

  /**
   * @param type the type of the event to listen for
   * @param listener the listener
   * @param <T> the type of cell
   * @param <U> the type of cell event
   * @param filter the cell filter apply. An event is not dispatched if no cell matches the filter
   * @return a registration that will dispose of the listener
   */
  public <T extends Cell, U extends CellEvent<T>> Registration addCellListener(
      CanvasEventType type, CellListener<T, U> listener, Predicate<? super T> filter) {
    if (ready) {
      return registerCellListener(type, listener, filter);
    } else {
      return registerPendingCellListener(type, listener, filter);
    }
  }

  /**
   * add a listener for canvas events. A canvas event is an event that is fired by the canvas
   * (rather than a cell).
   *
   * @param type the type of the event to listen for
   * @param listener the listener to add
   * @return a registration disposing of the listener
   */
  public Registration addCanvasListener(
      CanvasEventType type, CanvasListener<CanvasEvent> listener) {
    if (ready) {
      return registerCanvasListener(type, listener);
    } else {
      return registerPendingCanvasListener(type, listener);
    }
  }

  /**
   * The canvas is intended to be dynamically extended, and we don't want to add hundreds of methods
   * to the canvas.
   *
   * @param action the action to invoke
   * @param arguments the arguments to supply to the action
   * @param <T> the type of the response
   * @return a client-result containing the response
   */
  public <T> ClientResult<T> invoke(Class<? extends CanvasAction<T>> action, Object... arguments) {
    return ClientMethods.withUiSupplier(this).construct(action, arguments).apply(getModel());
  }

  /**
   * dispatched when the canvas is ready
   *
   * @param listener
   * @return the registration to dispose of the listener
   */
  public Registration addOnCanvasReadyListener(ComponentEventListener<CanvasReadyEvent> listener) {
    return addListener(CanvasReadyEvent.class, listener);
  }

  /** @return the canvas model for this canvas */
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

  private CanvasEventType resolveEventFor(CellDefinition cellDefinition) {
    switch (cellDefinition.getType()) {
      case Vertex:
        return VertexEvent.EventType.resolve(cellDefinition.getTargetEventType());
      case None:
        return CanvasInteractionEventType.resolve(cellDefinition.getTargetEventType());
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
              CanvasEventListener.CATEGORY,
              type.getType());
      definitions.add(definition);
      model.addEventListener(pendingListener.getDelegate(), type::getKey);
      val registration = getRegistration();
      pendingListener.setRegistration(registration);
    }
    invoke(AddListenersAction.class, definitions);
  }

  /**
   * register a listener before the canvas is ready
   *
   * @param type the type of the cell event
   * @param listener the event
   * @param filter the filter to search for matching cells
   * @param <T> the type of the cell
   * @param <U> the type of the cell Event
   * @return a registration to dispose of the listener with
   */
  private <T extends Cell, U extends CellEvent<T>> Registration registerPendingCellListener(
      CanvasEventType type, CellListener<T, U> listener, Predicate<? super T> filter) {
    val delegate = createCellEventListener(listener);
    val pending =
        new PendingCanvasEventListenerDescriptor<>(type, type.getCellType(), delegate, this);
    pendingListeners.add(pending);
    return pending;
  }

  /**
   * register a cell listener
   *
   * @param type the event type to listen for
   * @param listener the listener to apply upon a matching event
   * @param filter a cell filter to apply
   * @param <T> the type of the cell
   * @param <U> the type of the cell event
   * @return a registration to dispose of this listener with
   */
  private <T extends Cell, U extends CellEvent<T>> Registration registerCellListener(
      CanvasEventType type, CellListener<T, U> listener, Predicate<? super T> filter) {

    val definition =
        new ListenerDefinition(
            SharedGraphModel.identifierSequence.next(),
            type.getMappedName(),
            type.getCellType(),
            type.getCategory(),
            type.getType());
    val delegate = createCellEventListener(listener);
    invoke(AddListenerAction.class, definition);
    model.addEventListener(delegate, type::getKey);

    val registration = getRegistration();
    return () -> {
      model.removeEventListener(delegate);
      registration.remove();
    };
  }

  /**
   * synchronously register a canvas listener
   *
   * @param type
   * @param listener
   * @return a registration to dispose of the listener
   */
  private Registration registerCanvasListener(
      CanvasEventType type, CanvasListener<CanvasEvent> listener) {
    val definition =
        new ListenerDefinition(
            SharedGraphModel.identifierSequence.next(),
            type.getMappedName(),
            type.getCellType(),
            type.getCategory(),
            type.getType());
    val delegate = createCanvasEventListener(listener);
    invoke(AddListenerAction.class, definition);
    model.addEventListener(delegate, type::getKey);
    val registration = getRegistration();
    return () -> {
      model.removeEventListener(delegate);
      registration.remove();
    };
  }

  /**
   * register a listener before the canvas is ready. When the canvas is ready, the listener will be
   * added
   *
   * @param type the type of the canvas event to add
   * @param listener the listener
   * @return a registration that will dispose of the listener
   */
  private Registration registerPendingCanvasListener(
      CanvasEventType type, CanvasListener<CanvasEvent> listener) {

    val delegate = createCanvasEventListener(listener);
    val pending =
        new PendingCanvasEventListenerDescriptor<>(type, type.getCellType(), delegate, this);
    pendingListeners.add(pending);
    return pending;
  }

  /**
   * convenience method for creating a cell event listener
   *
   * @param listener the listener to delegate to
   * @param <T> the type of the cell
   * @param <U> the type of the cell event
   * @return a delegated listener
   */
  @NonNull
  private <T extends Cell, U extends CellEvent<T>> EventListener<U> createCellEventListener(
      CellListener<T, U> listener) {
    return (eventType, event) -> listener.on(event.getTarget());
  }

  /**
   * convenience method for creating a canvas event listener
   *
   * @param listener the listener to delegate to
   * @return a delegated listener
   */
  @NonNull
  private EventListener<CanvasEvent> createCanvasEventListener(
      CanvasListener<CanvasEvent> listener) {
    return (eventType, event) -> listener.on((CanvasEvent) event);
  }

  /**
   * all events are multiplexted through the canvas event listener
   *
   * @return the registration associated with the canvas event listener
   */
  private Registration getRegistration() {
    var registration = cellEventCountdownRegistration.get();
    if (registration == null) {
      cellEventCountdownRegistration.set(
          registration =
              new CountdownRegistration(
                  addListener(
                      CanvasEventListener.class,
                      event -> {
                        val cellDefinition = event.getValue();
                        val mappedEventType = resolveEventFor(cellDefinition);
                        if (mappedEventType.getCellType() == Cell.Type.None) {
                          model.dispatchEvent(
                              mappedEventType::getKey, createCanvasEvent(cellDefinition));
                        } else {
                          fireCellEvent(cellDefinition, mappedEventType);
                        }
                      })));
    }
    return registration;
  }

  private CanvasInteractionEvent createCanvasEvent(CellDefinition cellDefinition) {
    return new CanvasInteractionEvent(this, cellDefinition.getLocation());
  }

  private void fireCellEvent(CellDefinition cellDefinition, CanvasEventType mappedEventType) {
    model
        .findCell(cellDefinition.getType(), v -> Objects.equals(cellDefinition.getId(), v.getId()))
        .ifPresent(
            v ->
                model.dispatchEvent(mappedEventType::getKey, () -> createEvent(v, cellDefinition)));
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
