package com.aire.ux;

import com.aire.ux.RouteDefinition.Mode;
import com.aire.ux.actions.ActionManager;
import com.aire.ux.concurrency.AccessQueue;
import com.aire.ux.ext.ExtensionRegistry;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.HasElement;
import com.vaadin.flow.component.UI;
import java.util.Optional;
import java.util.function.Supplier;
import lombok.NonNull;

public class DefaultUserInterface implements UserInterface {

  private final AccessQueue accessQueue;
  private final ActionManager actionManager;
  private final ExtensionRegistry registry;

  public DefaultUserInterface(
      @NonNull final ExtensionRegistry registry, @NonNull AccessQueue accessQueue,
      @NonNull ActionManager actionManager) {
    this.registry = registry;
    this.accessQueue = accessQueue;
    this.actionManager = actionManager;
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
  public <T> Optional<T> selectFirst(PartialSelection<T> path, Supplier<UI> uiSupplier) {
    return path.select(this, uiSupplier);
  }

  @Override
  public <T extends HasElement> Registration register(
      PartialSelection<T> path, Extension<T> extension) {
    return registry.register(path, extension);
    //    return registry.register(path, () -> path.select(this, UI::getCurrent)
    //            .orElseThrow(
    //                () -> new NoSuchElementException(format("Could not find element at path: %s",
    // path))),
    //        extension);
  }

  @Override
  public <T extends Component> ExtensionRegistration register(Mode mode, Class<T> type) {
    return registry.register(new DefaultRouteDefinition(mode, type));
  }
}
