package io.sunshower.zephyr.ui.editor.actions;

import com.vaadin.flow.component.UI;
import io.sunshower.zephyr.ui.editor.EditorState;
import io.sunshower.zephyr.ui.rmi.AbstractClientMethodBoundAction;
import io.sunshower.zephyr.ui.rmi.ClientResult;
import java.util.function.Supplier;
import lombok.NonNull;

public class SetEditorContentsAction extends AbstractClientMethodBoundAction<String, EditorState>  {


  static final String METHOD_NAME = "setContents";
  static final String NAME = "editor:actions:contents:set";
  private final String value;


  public SetEditorContentsAction(Supplier<UI> supplier, @NonNull String value) {
    super(NAME, supplier, METHOD_NAME, String.class, String.class);
    this.value = value;
  }

  @Override
  public void undo(EditorState model) {

  }

  @Override
  public void redo(EditorState model) {

  }

  @Override
  public ClientResult<String> apply(EditorState model) {
    return ClientResult.create(String.class, method.invoke(model.getEditor(), value));
  }

}
