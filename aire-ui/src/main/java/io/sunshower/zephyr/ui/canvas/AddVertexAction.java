package io.sunshower.zephyr.ui.canvas;

import io.sunshower.persistence.id.Identifier;
import io.sunshower.persistence.id.Identifiers;
import io.sunshower.persistence.id.Sequence;
import lombok.NonNull;

public class AddVertexAction implements Action<Vertex> {

  static final String key = "actions:cells:vertex:add";
  static final Sequence<Identifier> idSequence;

  static {
    idSequence = Identifiers.newSequence();
  }

  private final Vertex vertex;

  public AddVertexAction(@NonNull Vertex vertex) {
    this.vertex = vertex;
    vertex.setId(idSequence.next());
  }

  @Override
  public String getKey() {
    return key;
  }

  @Override
  public void undo(Model model) {
  }

  @Override
  public void redo(Model model) {
  }

  @Override
  public ClientResult<Vertex> apply(Model model) {
    return null;
  }
}
