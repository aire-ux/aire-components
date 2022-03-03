package com.aire.ux;

import com.vaadin.flow.component.UI;
import java.util.Optional;
import java.util.function.Supplier;

class PartialPathSelection<T> implements PartialSelection<T> {

  private final String path;
  private final Class<T> type;

  public PartialPathSelection(String path, Class<T> type) {
    this.path = path;
    this.type = type;
  }

  @Override
  public Optional<T> select(UserInterface ui, Supplier<UI> supplier) {
    return new PathSelection<>(ui, path, type).select(supplier);
  }
}
