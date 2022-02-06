package io.sunshower.zephyr.ui.canvas.actions;

import com.vaadin.flow.component.UI;
import io.sunshower.zephyr.ui.canvas.Edge;
import io.sunshower.zephyr.ui.canvas.Model;
import io.sunshower.zephyr.ui.canvas.Vertex;
import io.sunshower.zephyr.ui.rmi.Argument;
import io.sunshower.zephyr.ui.rmi.ClientResult;
import java.util.List;
import java.util.function.Supplier;
import lombok.NonNull;

@Argument(collection = true, type = Edge.class)
public class ConnectVerticesAction extends AbstractClientMethodBoundAction<List<Edge>> {


  static final String NAME = "actions:edges:add";
  static final String METHOD_NAME = "connectVertices";
  private final List<Edge> edges;

  public ConnectVerticesAction(Supplier<UI> supplier, @NonNull List<Edge> edges) {
    super(NAME, supplier, METHOD_NAME, String.class, List.class);
    this.edges = edges;
  }

  @Override
  public void undo(Model model) {

  }

  @Override
  public void redo(Model model) {

  }

  @Override
  public ClientResult<List<Edge>> apply(Model model) {
    model.getCommandManager().apply(this);
    model.connectAll(edges);
    return ClientResult.createList(Edge.class, method.invoke(model.getHost(), edges));
  }
}
