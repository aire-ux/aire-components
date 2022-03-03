package com.aire.ux.ext;

import com.aire.ux.Extension;
import com.aire.ux.Registration;
import com.aire.ux.PartialSelection;
import com.vaadin.flow.component.HasElement;

public interface ExtensionRegistry {

  Class<?> typeOf(Object type);

  <T extends HasElement> Registration register(
      PartialSelection<T> path, Extension<T> extension);

  boolean isRegistered(Class<?> type);

  void decorate(Class<?> type, HasElement component);

  int getExtensionCount();
}
