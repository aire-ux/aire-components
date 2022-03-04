package com.aire.ux.ext;

import com.aire.ux.Extension;
import com.aire.ux.ExtensionRegistration;
import com.aire.ux.PartialSelection;
import com.aire.ux.RouteDefinition;
import com.vaadin.flow.component.HasElement;
import com.vaadin.flow.server.RouteRegistry;

public interface ExtensionRegistry extends RouteRegistry {

  Class<?> typeOf(Object type);

  <T extends HasElement> ExtensionRegistration register(
      PartialSelection<T> path, Extension<T> extension);

  <T extends HasElement> ExtensionRegistration register(RouteDefinition routeDefinition);

  boolean isRegistered(Class<?> type);

  void decorate(Class<?> type, HasElement component);

  int getExtensionCount();
}
