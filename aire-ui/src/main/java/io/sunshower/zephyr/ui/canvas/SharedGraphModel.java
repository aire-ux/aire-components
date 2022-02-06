package io.sunshower.zephyr.ui.canvas;

import com.google.common.collect.Streams;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.shared.Registration;
import io.sunshower.lang.events.AbstractEventSource;
import io.sunshower.persistence.id.Identifier;
import io.sunshower.zephyr.ui.canvas.Cell.Type;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.NonNull;
import lombok.val;

class SharedGraphModel extends AbstractEventSource
    implements Model, ComponentEventListener<CanvasReadyEvent> {

  /** constants */
  static final String format = "json";

  /** mutable state */
  private Canvas host;

  private List<Edge> edges;
  private List<Vertex> vertices;
  private CommandManager commandManager;
  private List<VertexTemplate> vertexTemplates;
  private VertexTemplate defaultVertexTemplate;
  private Registration canvasReadyEventRegistration;

  SharedGraphModel() {
    this.edges = new ArrayList<>();
    this.vertices = new ArrayList<>();
    this.vertexTemplates = new ArrayList<>();
  }

  SharedGraphModel(@NonNull final Canvas host) {
    this();
    this.host = host;
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
    return Streams.concat(vertices.stream(), edges.stream()).collect(Collectors.toList());
  }

  @Override
  @SuppressWarnings({"unchecked", "rawtypes"})
  public List<Cell> getCells(Type type) {
    switch (type) {
      case Vertex:
        return (List<Cell>) (List) vertices;
      case Edge:
        return (List<Cell>) (List) edges;
    }
    throw new IllegalArgumentException("Unknown type: " + type);
  }

  @Override
  public Identifier add(Cell cell) {
    if (cell.getType() == Type.Vertex) {
      vertices.add((Vertex) cell);
    } else {
      edges.add((Edge) cell);
    }
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
    return vertices;
  }

  @Override
  public void setVertices(Collection<Vertex> vertices) {
    this.vertices = new ArrayList<>(vertices);
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
  public List<VertexTemplate> getVertexTemplates() {
    return vertexTemplates;
  }

  @Override
  public void addVertexTemplate(VertexTemplate template) {
    this.vertexTemplates.add(template);
  }

  @Override
  public Optional<VertexTemplate> removeVertexTemplate(VertexTemplate template) {
    if (vertexTemplates.remove(template)) {
      return Optional.of(template);
    }
    return Optional.empty();
  }

  @Override
  public void addVertices(List<Vertex> vertices) {
    for (val vertex : vertices) {
      add(vertex);
    }
  }

  @Override
  public void onComponentEvent(CanvasReadyEvent event) {
    commandManager.applyPendingActions(false);
    commandManager.clearPendingActions();
  }
}
