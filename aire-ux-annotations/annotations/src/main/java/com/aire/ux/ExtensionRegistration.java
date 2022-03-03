package com.aire.ux;

public interface ExtensionRegistration extends AutoCloseable {

  void remove();

  default void close() {
    remove();
  }

}
