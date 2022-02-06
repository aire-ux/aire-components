package io.sunshower.zephyr.ui.canvas;

import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.shared.Registration;
import io.sunshower.gyre.AbstractDirectedGraph;
import io.sunshower.gyre.DirectedGraph;
import io.sunshower.gyre.DirectedGraph.Direction;
import io.sunshower.gyre.EdgeFilters;
import io.sunshower.gyre.Graph;
import io.sunshower.lang.events.AbstractEventSource;
import io.sunshower.persistence.id.Identifier;
import io.sunshower.persistence.id.Identifiers;
import io.sunshower.persistence.id.Sequence;
import io.sunshower.zephyr.ui.canvas.Cell.Type;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import lombok.NonNull;
import lombok.val;

class SharedGraphModel extends AbstractEventSource
    implements Model, ComponentEventListener<CanvasReadyEvent> {

  /** constants */
  static final String format = "json";

  static final Sequence<Identifier> identifierSequence;

  static {
    identifierSequence = Identifiers.newSequence();
  }

  /** mutable state */
  private Canvas host;

  private CommandManager commandManager;
  private List<VertexTemplate> vertexTemplates;
  private VertexTemplate defaultVertexTemplate;
  private Registration canvasReadyEventRegistration;
  private Graph<DirectedGraph.Edge<Edge>, Vertex> graph;

  SharedGraphModel() {
    this.graph = new AbstractDirectedGraph<>();
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
    return Stream.concat(
            graph.edgeSet().stream().map(DirectedGraph.Edge::getLabel), graph.vertexSet().stream())
        .collect(Collectors.toList());
  }

  @Override
  @SuppressWarnings({"unchecked", "rawtypes"})
  public List<Cell> getCells(Type type) {
    if (type == Type.Edge) {
      return graph.edgeSet().stream()
          .map(DirectedGraph.Edge::getLabel)
          .collect(Collectors.toList());
    }
    return List.copyOf(graph.vertexSet());
  }

  @Override
  @SuppressWarnings("PMD")
  public Edge connect(@NonNull Identifier source, @NonNull Identifier target) {
    val ids = Set.of(source, target);
    val vertices =
        graph.vertexSet().stream()
            .filter(vertex -> ids.contains(vertex.getId()))
            .collect(Collectors.toList());
    if (ids.size() != vertices.size()) {
      val existingIds = vertices.stream().map(Vertex::getId).collect(Collectors.toSet());
      throw new IllegalStateException(
          String.format(
              "Error: expected ids to be present ('%s'), actual ids: '%s'", ids, existingIds));
    }
    val sv = vertices.get(0);
    val tv = vertices.get(1);
    return connect(sv, tv);
  }

  @Override
  public Edge connect(Vertex source, Vertex target) {
    val vertices = graph.vertexSet();
    if (!vertices.contains(source)) {
      addVertex(source);
    }
    if (!vertices.contains(target)) {
      addVertex(target);
    }
    val edge =
        new DirectedGraph.DirectedEdge<>(
            new Edge(source.getId(), target.getId()), Direction.Outgoing);
    graph.connect(source, target, edge);
    return edge.getLabel();
  }

  @Override
  public void addVertex(@NonNull Vertex vertex) {
    graph.add(vertex);
  }

  @Override
  public List<Edge> getEdges(Identifier vertex) {
    return graph.vertexSet().stream()
        .filter(v -> v.getId().equals(vertex))
        .findAny()
        .map(
            v ->
                graph.adjacentEdges(v).stream()
                    .map(DirectedGraph.Edge::getLabel)
                    .collect(Collectors.toList()))
        .orElse(Collections.emptyList());
  }

  @NonNull
  @Override
  public List<Edge> getEdges(Vertex vertex) {
    return graph.getDependents(vertex, EdgeFilters.acceptAll()).stream()
        .map(DirectedGraph.Edge::getLabel)
        .toList();
  }

  @Override
  public List<Vertex> getVertices() {
    return List.copyOf(graph.vertexSet());
  }

  @Override
  public void setVertices(Collection<Vertex> vertices) {
    if (graph == null) {
      graph = new AbstractDirectedGraph<Edge, Vertex>();
    }
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
      graph.add(vertex);
    }
  }

  @Override
  public void onComponentEvent(CanvasReadyEvent event) {
    commandManager.applyPendingActions(false);
    commandManager.clearPendingActions();
  }
}
