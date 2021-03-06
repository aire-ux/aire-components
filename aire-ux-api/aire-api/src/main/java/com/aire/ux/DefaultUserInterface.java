package com.aire.ux;

import com.aire.ux.RouteDefinition.Scope;
import com.aire.ux.actions.ActionManager;
import com.aire.ux.concurrency.AccessQueue;
import com.aire.ux.ext.ExtensionRegistry;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.HasElement;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.server.ServiceException;
import com.vaadin.flow.server.SessionDestroyEvent;
import com.vaadin.flow.server.SessionInitEvent;
import com.vaadin.flow.server.VaadinSession;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;
import lombok.NonNull;
import lombok.val;

public class DefaultUserInterface implements UserInterface {

  private final AccessQueue accessQueue;
  private final ActionManager actionManager;
  private final ExtensionRegistry registry;

  private final List<VaadinSession> activeUis;

  public DefaultUserInterface(
      @NonNull final ExtensionRegistry registry,
      @NonNull AccessQueue accessQueue,
      @NonNull ActionManager actionManager) {
    this.registry = registry;
    this.accessQueue = accessQueue;
    this.actionManager = actionManager;
    this.activeUis = new ArrayList<>();
  }

  @Override
  public ActionManager getActionManager() {
    return actionManager;
  }

  @Override
  public ExtensionRegistry getExtensionRegistry() {
    return registry;
  }

  @Override
  public void reload() {
    synchronized (activeUis) {
      for (val session : activeUis) {
        session.access(
            () -> {
              val uis = session.getUIs();
              synchronized (uis) {
                for (val ui : uis) {
                  ui.access(() -> ui.getPage().reload());
                }
              }
            });
      }
    }
  }

  @Override
  public <T> Optional<T> selectFirst(PartialSelection<T> path, Supplier<UI> uiSupplier) {
    return path.select(this, uiSupplier, extensionFor(path)).map(ExtensionDefinition::getValue);
  }

  @Override
  public <T extends HasElement> Registration register(
      PartialSelection<T> path, Extension<T> extension) {
    val result = registry.register(path, extension);
    Optional.ofNullable(UI.getCurrent()).ifPresent(UI::push);
    return result;
  }

  @Override
  public <T extends Component> ExtensionRegistration register(Scope scope, Class<T> type) {
    return registry.register(new DefaultRouteDefinition(List.of(scope), type));
  }

  private <T> Extension<T> extensionFor(PartialSelection<T> path) {
    return new DefaultComponentExtension<>(path.getSegment(), c -> {});
  }

  @Override
  public void sessionDestroy(SessionDestroyEvent event) {
    synchronized (activeUis) {
      val session = event.getSession();
      activeUis.remove(session);
    }
  }

  @Override
  public void sessionInit(SessionInitEvent event) throws ServiceException {
    synchronized (activeUis) {
      val session = event.getSession();
      activeUis.add(session);
    }
  }
}
