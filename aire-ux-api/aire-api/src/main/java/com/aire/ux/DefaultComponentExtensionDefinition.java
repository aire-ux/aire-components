package com.aire.ux;

public class DefaultComponentExtensionDefinition<T> implements ExtensionDefinition<T> {

  private final String path;
  private final T value;
  private final Extension<T> extension;
  private final Selection<T> selection;

  public DefaultComponentExtensionDefinition(
      String path, Extension<T> extension, Selection<T> selection, T value) {
    this.path = path;
    this.value = value;
    this.extension = extension;
    this.selection = selection;
  }

  @Override
  public String getPath() {
    return path + extension.getSegment();
  }

  @Override
  public T getValue() {
    return value;
  }

  @Override
  public Extension<T> getExtension() {
    return extension;
  }

  @Override
  public Selection<T> getSelection() {
    return selection;
  }

  @Override
  public Class<T> getType() {
    return selection.getType();
  }
}
