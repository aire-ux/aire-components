package com.aire.ux;

public interface Extension<T> {
  String getSegment();

  /**
   *
   * @return the "source" for this extension (whatever class contributed it, or nothing)
   */
  Class<?> getSource();

  void decorate(T value);
}
