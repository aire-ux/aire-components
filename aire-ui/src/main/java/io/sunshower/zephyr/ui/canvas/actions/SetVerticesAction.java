package io.sunshower.zephyr.ui.canvas.actions;

import io.sunshower.zephyr.ui.Elements;
import io.sunshower.zephyr.ui.canvas.AbstractUIAccessingAction;
import io.sunshower.zephyr.ui.canvas.Model;
import io.sunshower.zephyr.ui.canvas.Vertex;
import java.util.Collection;

public class SetVerticesAction extends AbstractUIAccessingAction {

  static final String NAME = "actions:vertices:set";

  static final String FUNCTION_NAME = "addVertices";
  private final Collection<Vertex> vertices;

  public SetVerticesAction(Collection<Vertex> vertices) {
    super(NAME);
    this.vertices = vertices;
  }

  @Override
  public void undo(Model model) {}

  @Override
  public void redo(Model model) {}

  @Override
  public void apply(Model model) {
    access(model)
        .accept(
            (ui, canvas, m) -> {
              Elements.inClient(
                  FUNCTION_NAME, canvas.getElement(), Elements.writeAll(Vertex.class, vertices));
              return null;
            });
  }
}
