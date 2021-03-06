package com.aire.ux;

import com.vaadin.flow.component.UI;
import java.util.Optional;
import java.util.function.Supplier;

public interface Selection<T> {

  Class<T> getType();

  static <T> PartialSelection<T> path(String path, Class<T> type) {
    return new PartialPathSelection<>(path, type);
  }

  @SuppressWarnings("unchecked")
  static <T> PartialSelection<T> path(String s) {
    return path(s, (Class<T>) Object.class);
  }

  Optional<ExtensionDefinition<T>> select(Supplier<UI> supplier, Extension<T> extension);
}
