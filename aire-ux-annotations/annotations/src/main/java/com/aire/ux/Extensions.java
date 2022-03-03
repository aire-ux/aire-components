package com.aire.ux;

import java.util.function.Consumer;

public class Extensions {

  public static <T> Extension<T> create(String path, Consumer<T> consumer) {
    return new DefaultComponentExtension<>(path, consumer);
  }

}
