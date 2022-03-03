package com.aire.ux.ext;

import com.aire.ux.Extension;
import com.aire.ux.ExtensionRegistration;
import com.aire.ux.PartialSelection;
import com.vaadin.flow.component.HasElement;
import java.util.function.Supplier;

public interface ExtensionRegistry {

  Class<?> typeOf(Object type);

  <T extends HasElement> ExtensionRegistration register(PartialSelection<T> path, Extension<T> extension);

  boolean isRegistered(Class<?> type);

  void decorate(Class<?> type, HasElement component);
}
