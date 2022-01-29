package io.sunshower.zephyr.ui.canvas;

import lombok.NonNull;

public class AddVertexAction implements Action {

  static final String key = "actions:cells:vertex:add";

  private final Vertex vertex;

  public AddVertexAction(@NonNull Vertex vertex) {
    this.vertex = vertex;
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
  public void apply(Model model) {
  }
}
