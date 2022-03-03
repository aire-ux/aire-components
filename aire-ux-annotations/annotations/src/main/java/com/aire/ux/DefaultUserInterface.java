package com.aire.ux;

import com.aire.ux.concurrency.AccessQueue;
import com.aire.ux.ext.ExtensionRegistry;
import com.vaadin.flow.component.HasElement;
import com.vaadin.flow.component.UI;
import java.util.Optional;
import java.util.function.Supplier;
import lombok.NonNull;

public class DefaultUserInterface implements UserInterface {

  private final AccessQueue accessQueue;
  private final ExtensionRegistry registry;

  public DefaultUserInterface(
      @NonNull final ExtensionRegistry registry, @NonNull AccessQueue accessQueue) {
    this.registry = registry;
    this.accessQueue = accessQueue;
  }

  @Override
  public ExtensionRegistry getExtensionRegistry() {
    return registry;
  }

  @Override
  public <T> Optional<T> selectFirst(PartialSelection<T> path, Supplier<UI> uiSupplier) {
    return path.select(this, uiSupplier);
  }

  @Override
  public <T extends HasElement> ExtensionRegistration register(PartialSelection<T> path,
      Extension<T> extension) {
    return registry.register(path, extension);
//    return registry.register(path, () -> path.select(this, UI::getCurrent)
//            .orElseThrow(
//                () -> new NoSuchElementException(format("Could not find element at path: %s", path))),
//        extension);
  }
}
