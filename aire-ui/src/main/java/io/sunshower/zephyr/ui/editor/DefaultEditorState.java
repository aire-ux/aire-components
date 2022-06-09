package io.sunshower.zephyr.ui.editor;

final class DefaultEditorState implements EditorState {

  private final Editor editor;
  private CharSequence contents;

  public DefaultEditorState(Editor editor) {
    this.editor = editor;
  }


  @Override
  public CharSequence getContents() {
    return contents;
  }

  @Override
  public void setContents(CharSequence contents) {
    this.contents = contents;

  }

  @Override
  public Editor getEditor() {
    return editor;
  }
}
