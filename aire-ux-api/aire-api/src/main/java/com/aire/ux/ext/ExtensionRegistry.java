package com.aire.ux.ext;

import com.aire.ux.ComponentInclusionManager;
import com.aire.ux.Extension;
import com.aire.ux.ExtensionDefinition;
import com.aire.ux.ExtensionRegistration;
import com.aire.ux.PartialSelection;
import com.aire.ux.RouteDefinition;
import com.vaadin.flow.component.HasElement;
import com.vaadin.flow.server.RouteRegistry;
import io.sunshower.lang.events.EventSource;
import io.sunshower.lang.events.EventType;
import java.util.List;
import javax.annotation.concurrent.ThreadSafe;
import lombok.NonNull;

@ThreadSafe
public interface ExtensionRegistry extends RouteRegistry, EventSource, AutoCloseable {

  Class<?> typeOf(Object type);

  <T extends HasElement> ExtensionRegistration register(
      PartialSelection<T> path, Extension<T> extension);

  <T extends HasElement> ExtensionRegistration register(RouteDefinition routeDefinition);

  boolean isRegistered(Class<?> type);

  void decorate(Class<?> type, HasElement component);

  int getExtensionCount();

  List<ExtensionDefinition<?>> getExtensions();

  @NonNull
  ComponentInclusionManager getComponentInclusionManager();

  enum Events implements EventType {
    RouteRegistered,
    RouteUnregistered,
    ExtensionRegistered,
    ExtensionUnregistered;

    private final int id;

    Events() {
      this.id = EventType.newId();
    }

    @Override
    public int getId() {
      return id;
    }
  }
}
