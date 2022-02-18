package io.sunshower.zephyr.ui.canvas.actions;

import com.vaadin.flow.component.UI;
import io.sunshower.zephyr.ui.canvas.Model;
import io.sunshower.zephyr.ui.canvas.Vertex;
import io.sunshower.zephyr.ui.rmi.AbstractClientMethodBoundAction;
import io.sunshower.zephyr.ui.rmi.ClientResult;
import java.util.function.Supplier;
import lombok.NonNull;

public class AddVertexAction extends AbstractClientMethodBoundAction<Vertex, Model> {

  static final String NAME = "actions:cells:vertex:add";
  static final String METHOD_NAME = "addVertex";

  private final Vertex vertex;

  public AddVertexAction(Supplier<UI> supplier, @NonNull Vertex vertex) {
    super(NAME, supplier, METHOD_NAME, String.class, Vertex.class);
    this.vertex = vertex;
  }

  public AddVertexAction(@NonNull Vertex vertex) {
    super(NAME, METHOD_NAME, String.class, Vertex.class);
    this.vertex = vertex;
  }

  @Override
  public void undo(Model model) {}

  @Override
  public void redo(Model model) {}

  @Override
  public ClientResult<Vertex> apply(Model model) {
    model.getCommandManager().apply(this);
    model.addVertex(vertex);
    return ClientResult.create(Vertex.class, method.invoke(model.getHost(), vertex));
  }
}
