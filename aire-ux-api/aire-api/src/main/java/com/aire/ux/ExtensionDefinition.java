package com.aire.ux;

public interface ExtensionDefinition<T> {

  String getPath();

  T getValue();

  Extension<T> getExtension();

  Selection<T> getSelection();

  Class<T> getType();
}
