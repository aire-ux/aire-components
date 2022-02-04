package io.sunshower.zephyr.ui.canvas.actions;

import com.vaadin.flow.component.UI;
import io.sunshower.persistence.id.Identifier;
import io.sunshower.persistence.id.Identifiers;
import io.sunshower.persistence.id.Sequence;
import io.sunshower.zephyr.ui.canvas.ClientResult;
import io.sunshower.zephyr.ui.canvas.Model;
import io.sunshower.zephyr.ui.canvas.Vertex;
import java.util.function.Supplier;
import lombok.NonNull;

public class AddVertexAction extends AbstractClientMethodBoundAction<Vertex> {

  static final String NAME = "actions:cells:vertex:add";
  static final String METHOD_NAME = "addVertex";
  static final Sequence<Identifier> idSequence;

  static {
    idSequence = Identifiers.newSequence();
  }

  private final Vertex vertex;

  public AddVertexAction(Supplier<UI> supplier, @NonNull Vertex vertex) {
    super(NAME, supplier, METHOD_NAME, String.class, Vertex.class);
    this.vertex = vertex;
    vertex.setId(idSequence.next());
  }

  public AddVertexAction(@NonNull Vertex vertex) {
    super(NAME, METHOD_NAME, String.class, Vertex.class);
    this.vertex = vertex;
    vertex.setId(idSequence.next());
  }

  @Override
  public void undo(Model model) {
  }

  @Override
  public void redo(Model model) {
  }

  @Override
  public ClientResult<Vertex> apply(Model model) {
    method.invoke(model.getHost(), vertex).then(System.out::println);
    return null;
  }

}
