package io.sunshower.zephyr.ui.editor.actions;

import com.vaadin.flow.component.UI;
import io.sunshower.zephyr.ui.editor.EditorState;
import io.sunshower.zephyr.ui.rmi.AbstractClientMethodBoundAction;
import io.sunshower.zephyr.ui.rmi.ClientResult;
import java.util.function.Supplier;

public class GetEditorContentsAction extends AbstractClientMethodBoundAction<String, EditorState> {

  static final String METHOD_NAME = "getContents";
  static final String NAME = "editor:actions:contents:get";

  public GetEditorContentsAction(Supplier<UI> supplier) {
    super(NAME, supplier, METHOD_NAME, String.class, String.class);
  }

  @Override
  public void undo(EditorState model) {}

  @Override
  public void redo(EditorState model) {}

  @Override
  public ClientResult<String> apply(EditorState model) {
    return ClientResult.create(String.class, method.invoke(model.getEditor()));
  }
}
