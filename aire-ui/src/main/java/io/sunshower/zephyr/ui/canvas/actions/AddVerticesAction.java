package io.sunshower.zephyr.ui.canvas.actions;

import com.vaadin.flow.component.UI;
import io.sunshower.zephyr.ui.ClientResult;
import io.sunshower.zephyr.ui.canvas.Model;
import io.sunshower.zephyr.ui.canvas.Vertex;
import java.util.List;
import java.util.function.Supplier;

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
    //    method.invoke(model.getHost(), vertices).then(System.out::println);
    return null;
  }
}
