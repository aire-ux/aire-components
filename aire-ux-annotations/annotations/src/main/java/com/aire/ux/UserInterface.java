package com.aire.ux;

import com.aire.ux.ext.ExtensionRegistry;
import com.vaadin.flow.component.HasElement;
import com.vaadin.flow.component.UI;
import java.util.Optional;
import java.util.ServiceLoader;
import java.util.ServiceLoader.Provider;
import java.util.function.Supplier;

public interface UserInterface {

  //  <T> Optional<T> selectAny(Selection selection);
  //  <T> Optional<T> selectFirst(Selection selection);
  //  <T> void withSelection(Selection selection, Consumer<T> action);
  //
  //
  //  <T> ExtensionRegistration register(Selection s, Extension<T> extension);

  static Optional<UserInterface> getInstance() {
    return ServiceLoader.load(UserInterfaceProvider.class).stream()
        .map(Provider::get)
        .map(UserInterfaceProvider::get)
        .findAny();
  }

  ExtensionRegistry getExtensionRegistry();

  default <T> Optional<T> selectFirst(PartialSelection<T> path) {
    return selectFirst(path, UI::getCurrent);
  }

  <T> Optional<T> selectFirst(PartialSelection<T> path, Supplier<UI> uiSupplier);

  <T extends HasElement> ExtensionRegistration register(
      PartialSelection<T> path, Extension<T> extension);
}

class Holder {

  static ThreadLocal<UserInterface> instance = new ThreadLocal<>();
}
