package com.aire.ux;

import com.aire.ux.RouteDefinition.Scope;
import com.aire.ux.actions.ActionManager;
import com.aire.ux.concurrency.AccessQueue;
import com.aire.ux.concurrency.AccessQueue.Target;
import com.aire.ux.ext.ExtensionRegistry;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.HasElement;
import com.vaadin.flow.component.UI;
import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;
import lombok.NonNull;
import lombok.val;

public class DefaultUserInterface implements UserInterface {

  private final AccessQueue accessQueue;
  private final ActionManager actionManager;
  private final ExtensionRegistry registry;

  public DefaultUserInterface(
      @NonNull final ExtensionRegistry registry,
      @NonNull AccessQueue accessQueue,
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
  public void reload() {
    accessQueue.broadcast(
        Target.UI,
        ui -> {
          if (ui instanceof UI u) {
            u.getPage().reload();
          }
        });
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
}
