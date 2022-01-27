package io.sunshower.zephyr.ui.canvas.actions;

import io.sunshower.zephyr.ui.canvas.AbstractUIAccessingAction;
import io.sunshower.zephyr.ui.canvas.Model;

public class AddVertexTemplateAction extends AbstractUIAccessingAction {

  static final String NAME = "actions:vertices:templates:add";

  public AddVertexTemplateAction() {
    super(NAME);
  }

  @Override
  public void undo(Model model) {}

  @Override
  public void redo(Model model) {}

  @Override
  public void apply(Model model) {}
}
