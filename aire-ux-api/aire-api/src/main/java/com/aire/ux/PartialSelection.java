package com.aire.ux;

import com.vaadin.flow.component.HasElement;
import com.vaadin.flow.component.UI;
import java.util.Optional;
import java.util.function.Supplier;

public interface PartialSelection<T> {

  boolean isHostedBy(Class<?> type);

  Optional<ExtensionDefinition<T>> select(
      UserInterface ui, Supplier<UI> supplier, Extension<T> extension);

  Optional<ExtensionDefinition<T>> select(
      HasElement component, UserInterface userInterface, Extension<T> extension);

  String getSegment();
}
