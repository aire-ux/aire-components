package com.aire.ux.actions;

import java.util.function.Consumer;

public class DefaultDelegatingAction extends AbstractAction {

  private final Consumer<Action> action;

  public DefaultDelegatingAction(Key of, Consumer<Action> action) {
    super(of, true);
    this.action = action;
  }

  @Override
  public void perform() {
    action.accept(this);
  }
}
