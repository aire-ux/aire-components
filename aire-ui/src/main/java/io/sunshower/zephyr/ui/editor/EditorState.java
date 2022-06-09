package io.sunshower.zephyr.ui.editor;

public interface EditorState {

  CharSequence getContents();
  public void setContents(CharSequence contents);

  Editor getEditor();
}
