package com.aire.ux;

import java.util.concurrent.atomic.AtomicReference;

public class Aire {

  private static final AtomicReference<UserInterface> userinterface;

  static {
    userinterface = new AtomicReference<>();
  }

  public static <T> Selection<T> select(UserInterface ui, String path, Class<T> type) {
    return new PathSelection<T>(ui, path, type);
  }

  public static UserInterface getUserInterface() {
    return userinterface.get();
  }

  public static UserInterface setUserInterface(UserInterface userInterface) {
    userinterface.set(userInterface);
    return userInterface;
  }
}
