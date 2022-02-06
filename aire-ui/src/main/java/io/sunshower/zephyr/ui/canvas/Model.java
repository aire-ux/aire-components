package io.sunshower.zephyr.ui.canvas;

import com.vaadin.flow.component.ComponentEventListener;
import io.sunshower.lang.events.EventSource;
import io.sunshower.persistence.id.Identifier;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import lombok.NonNull;
import lombok.val;

public interface Model extends EventSource, ComponentEventListener<CanvasReadyEvent> {

  @NonNull
  static Model create(@NonNull Canvas canvas) {
    val model = new SharedGraphModel(canvas);
    canvas.setModel(model);
    return model;
  }

  VertexTemplate getDefaultVertexTemplate();

  void setDefaultVertexTemplate(VertexTemplate template);

  @NonNull
  Canvas getHost();

  void setHost(@NonNull Canvas host);

  @NonNull
  List<Cell> getCells();

  @NonNull
  List<Cell> getCells(@NonNull Cell.Type type);

  @NonNull
  Identifier add(@NonNull Cell cell);

  Edge connect(@NonNull Vertex source, @NonNull Vertex target);

  Edge connect(@NonNull Identifier source, @NonNull Identifier target);

  @NonNull
  List<Edge> getEdges(@NonNull Identifier vertex);

  @NonNull
  List<Edge> getEdges(@NonNull Vertex vertex);

  List<Vertex> getVertices();

  void setVertices(Collection<Vertex> vertices);

  void detach(Canvas canvas);

  void attach(Canvas canvas);

  CommandManager getCommandManager();

  List<VertexTemplate> getVertexTemplates();

  void addVertexTemplate(VertexTemplate template);

  Optional<VertexTemplate> removeVertexTemplate(VertexTemplate template);

  void addVertices(List<Vertex> vertices);
}
