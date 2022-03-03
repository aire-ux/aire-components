package com.aire.ux.actions;

import java.util.function.Consumer;

public class Actions {

  public static Action create(String path, Consumer<Action> action) {
    return new DefaultDelegatingAction(Key.of(path), action);
  }

}
