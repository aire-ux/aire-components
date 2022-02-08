package io.sunshower.zephyr.ui.canvas.actions;

import com.vaadin.flow.component.UI;
import io.sunshower.zephyr.ui.canvas.Model;
import io.sunshower.zephyr.ui.canvas.Vertex;
import io.sunshower.zephyr.ui.rmi.Argument;
import io.sunshower.zephyr.ui.rmi.ClientResult;
import java.util.List;
import java.util.function.Supplier;

@Argument(collection = true, type = Vertex.class)
public class AddVerticesAction extends AbstractClientMethodBoundAction<List<Vertex>> {

  static final String NAME = "actions:vertices:add";
  static final String METHOD_NAME = "addVertices";
  private final List<Vertex> vertices;

  public AddVerticesAction(Supplier<UI> supplier, List<Vertex> vertices) {
    super(NAME, supplier, METHOD_NAME, String.class, List.class);
    this.vertices = vertices;
  }

  @Override
  public void undo(Model model) {}

  @Override
  public void redo(Model model) {}

  @Override
  public ClientResult<List<Vertex>> apply(Model model) {
    model.getCommandManager().apply(this);
    model.addVertices(vertices);
    return ClientResult.createList(Vertex.class, method.invoke(model.getHost(), vertices));
  }
}
