package com.aire.ux;

import java.util.function.Consumer;

public class DefaultComponentExtension<T> implements Extension<T> {

  private final String segment;
  private final Consumer<T> consumer;

  public DefaultComponentExtension(String segment, Consumer<T> consumer) {
    this.segment = segment;
    this.consumer = consumer;
  }
}
