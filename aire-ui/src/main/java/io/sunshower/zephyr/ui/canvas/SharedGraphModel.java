package io.sunshower.zephyr.ui.canvas;

import com.aire.ux.condensation.Condensation;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.shared.Registration;
import io.sunshower.lang.events.AbstractEventSource;
import io.sunshower.persistence.id.Identifier;
import io.sunshower.zephyr.ui.canvas.Cell.Type;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import lombok.NonNull;

class SharedGraphModel extends AbstractEventSource
    implements Model, ComponentEventListener<CanvasReadyEvent> {

  /** constants */
  static final String format = "json";

  /** immutable state */
  private final Condensation condensation;

  /** mutable state */
  private Canvas host;

  private List<Vertex> vertices;
  private CommandManager commandManager;
  private VertexTemplate defaultVertexTemplate;
  private Registration canvasReadyEventRegistration;

  SharedGraphModel() {
    this.condensation = Condensation.create(format);
  }

  SharedGraphModel(@NonNull final Canvas host) {
    this(host, format);
  }

  SharedGraphModel(@NonNull final Canvas host, @NonNull String format) {
    this.host = host;
    this.condensation = Condensation.create(format);
  }

  @Override
  public VertexTemplate getDefaultVertexTemplate() {
    return defaultVertexTemplate;
  }

  @Override
  public void setDefaultVertexTemplate(VertexTemplate template) {
    this.defaultVertexTemplate = template;
  }

  @Override
  public @NonNull Canvas getHost() {
    if (host == null) {
      throw new IllegalStateException("Error: Graph Model has not been properly initialized");
    }
    return host;
  }

  @Override
  public void setHost(@NonNull Canvas host) {
    this.host = host;
    this.commandManager = new StackCommandManager(this, host);
  }

  @Override
  public List<Cell> getCells() {
    return null;
  }

  @Override
  public List<Cell> getCells(Type type) {
    return null;
  }

  @Override
  public Identifier add(Cell cell) {
    return null;
  }

  @Override
  public Edge connect(Identifier source, Identifier target) {
    return null;
  }

  @Override
  public Edge connect(Vertex source, Vertex target) {
    return null;
  }

  @Override
  public List<Edge> getEdges(Identifier vertex) {
    return null;
  }

  @Override
  public List<Edge> getEdges(Vertex vertex) {
    return null;
  }

  @Override
  public List<Vertex> getVertices() {
    return null;
  }

  @Override
  public void setVertices(Collection<Vertex> vertices) {
    this.vertices = new ArrayList<>(vertices);
//    commandManager.addPendingAction(
//        new CompositeAction(vertices.stream().map(AddVertexAction::new)));
  }

  @Override
  public void detach(Canvas canvas) {
    this.canvasReadyEventRegistration.remove();
  }

  @Override
  public void attach(Canvas canvas) {
    this.canvasReadyEventRegistration = canvas.addOnCanvasReadyListener(this);
  }

  @Override
  public CommandManager getCommandManager() {
    return commandManager;
  }

  @Override
  public void onComponentEvent(CanvasReadyEvent event) {
    commandManager.applyPendingActions(false);
    commandManager.clearPendingActions();
  }

  private Serializable write(Class<Vertex> vertexClass, Collection<Vertex> vertices)
      throws IOException {
    return condensation.getWriter().writeAll(vertexClass, vertices);
  }
}
