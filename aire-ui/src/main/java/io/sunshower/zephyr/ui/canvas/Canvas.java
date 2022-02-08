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
import io.sunshower.lang.events.Event;
import io.sunshower.lang.events.EventListener;
import io.sunshower.lang.events.EventType;
import io.sunshower.zephyr.ui.canvas.CanvasVertexEventListener.VertexDefinition;
import io.sunshower.zephyr.ui.canvas.actions.AddListenerAction;
import io.sunshower.zephyr.ui.canvas.listeners.CellListener;
import io.sunshower.zephyr.ui.canvas.listeners.ListenerDefinition;
import io.sunshower.zephyr.ui.canvas.listeners.VertexEvent;
import io.sunshower.zephyr.ui.canvas.listeners.VertexEvent.Type;
import io.sunshower.zephyr.ui.rmi.ClientMethods;
import io.sunshower.zephyr.ui.rmi.ClientResult;
import java.util.ArrayList;
import java.util.List;
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

  private Model model;
  private volatile boolean ready;
  private CommandManager commandManager;


  private final List<PendingVertexEventListenerDescriptor> pendingVertexEventListeners;

  public Canvas() {
    setModel(new SharedGraphModel());
    addOnCanvasReadyListener(this);
    pendingVertexEventListeners = new ArrayList<>();
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

  public Registration addVertexListener(VertexEvent.Type type, CellListener<Vertex> listener) {
    return addVertexListener(type, listener, v -> true);
  }

  public Registration addVertexListener(
      VertexEvent.Type type, CellListener<Vertex> listener, Predicate<Vertex> vertexFilter) {

    if (!ready) {
      return registerPendingVertexListener(type, listener, vertexFilter);
    } else {
      return doRegisterVertexListener(type, listener, vertexFilter);
    }


  }

  private Registration registerPendingVertexListener(Type type, CellListener<Vertex> listener,
      Predicate<Vertex> vertexFilter) {
    val pending = new PendingVertexEventListenerDescriptor(type, listener, vertexFilter);
    pendingVertexEventListeners.add(pending);
    return () -> {
      // TODO JOSIAH
    };

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
    bulkRegisterPendingVertexListeners();
  }

  private void bulkRegisterPendingVertexListeners() {

  }

  private Registration doRegisterVertexListener(
      VertexEvent.Type type, CellListener<Vertex> listener, Predicate<Vertex> vertexFilter
  ) {
    val definition = new ListenerDefinition(SharedGraphModel.identifierSequence.next(),
        type.getMappedName(), CanvasVertexEventListener.CATEGORY, type.getType());
    invoke(AddListenerAction.class, definition);
    val delegate = new EventListener<VertexDefinition>() {
      @Override
      public void onEvent(EventType eventType, Event<VertexDefinition> event) {
        model.findVertex(vertexFilter.and(v -> event.getTarget().getId().equals(v.getId())))
            .ifPresent(listener::on);
      }
    };
    model.addEventListener(delegate, type::ordinal);

    val registration = addListener(CanvasVertexEventListener.class, event -> {
      model.dispatchEvent(type::ordinal, event::getValue);
    });
    return () -> {
      registration.remove();
      model.removeEventListener(delegate);
    };
  }
}
