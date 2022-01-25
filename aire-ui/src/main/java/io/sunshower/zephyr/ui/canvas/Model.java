package io.sunshower.zephyr.ui.canvas;

import io.sunshower.lang.events.EventSource;
import io.sunshower.persistence.id.Identifier;
import java.util.List;
import lombok.NonNull;

public interface Model extends EventSource {

  @NonNull
  static Model create(@NonNull Canvas canvas) {
    return new SharedGraphModel(canvas);
  }

  @NonNull Canvas getHost();

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

  @NonNull List<Edge> getEdges(@NonNull Vertex vertex);

}
