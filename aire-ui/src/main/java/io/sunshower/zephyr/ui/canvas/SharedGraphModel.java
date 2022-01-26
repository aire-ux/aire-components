package io.sunshower.zephyr.ui.canvas;

import com.aire.ux.condensation.Condensation;
import io.sunshower.lang.events.AbstractEventSource;
import io.sunshower.persistence.id.Identifier;
import io.sunshower.zephyr.ui.canvas.Cell.Type;
import java.util.List;
import lombok.NonNull;

class SharedGraphModel extends AbstractEventSource implements Model {

  static final String format = "json";
  private final Condensation condensation;
  private Canvas host;

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
  public @NonNull Canvas getHost() {
    if (host == null) {
      throw new IllegalStateException("Error: Graph Model has not been properly initialized");
    }
    return host;
  }

  @Override
  public void setHost(@NonNull Canvas host) {
    this.host = host;
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
}
