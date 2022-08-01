package com.aire.ux;

import java.util.function.Consumer;

public class DefaultComponentExtension<T> implements Extension<T> {


  private final String segment;
  private final Class<?>  source;
  private final Consumer<T> consumer;

  public DefaultComponentExtension(String segment, Consumer<T> consumer) {
    this(segment, consumer, Object.class);
  }


  public DefaultComponentExtension(String segment, Consumer<T> consumer, Class<?> source) {
    this.segment = segment;
    this.source = source;
    this.consumer = consumer;
  }

  @Override
  public String getSegment() {
    return segment;
  }

  @Override
  public Class<?> getSource() {
    return source;
  }

  public void decorate(T value) {
    consumer.accept(value);
  }
}
