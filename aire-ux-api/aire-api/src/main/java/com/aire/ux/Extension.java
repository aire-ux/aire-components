package com.aire.ux;

public interface Extension<T> {
  String getSegment();

  void decorate(T value);
}
