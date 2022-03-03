package com.aire.ux;

public interface Registration extends AutoCloseable {

  void remove();

  default void close() {
    remove();
  }
}
