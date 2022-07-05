package com.aire.ux;

import com.aire.ux.RouteDefinition.Scope;
import com.aire.ux.actions.ActionManager;
import com.aire.ux.ext.ExtensionRegistry;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.HasElement;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.server.SessionDestroyListener;
import com.vaadin.flow.server.SessionInitListener;
import java.util.Optional;
import java.util.ServiceLoader;
import java.util.ServiceLoader.Provider;
import java.util.function.Supplier;
import javax.annotation.concurrent.ThreadSafe;
import lombok.NonNull;

@ThreadSafe
public interface UserInterface extends SessionInitListener, SessionDestroyListener {

  static Optional<UserInterface> getInstance() {
    return ServiceLoader.load(UserInterfaceProvider.class).stream()
        .map(Provider::get)
        .map(UserInterfaceProvider::get)
        .findAny();
  }

  ActionManager getActionManager();

  ExtensionRegistry getExtensionRegistry();

  default <T> Optional<T> selectFirst(PartialSelection<T> path) {
    return selectFirst(path, UI::getCurrent);
  }

  @NonNull
  default ComponentInclusionManager getComponentInclusionManager() {
    return getExtensionRegistry().getComponentInclusionManager();
  }

  /**
   * @param voter the ComponentInclusionVoter to register
   * @return a registration allowing to unregister the voter
   */
  @NonNull
  default Registration registerComponentInclusionVoter(@NonNull ComponentInclusionVoter voter) {
    return getComponentInclusionManager().register(voter);
  }

  void reload();

  <T> Optional<T> selectFirst(PartialSelection<T> path, Supplier<UI> uiSupplier);

  <T extends HasElement> Registration register(PartialSelection<T> path, Extension<T> extension);

  <T extends Component> ExtensionRegistration register(Scope scope, Class<T> type);
}

class Holder {

  static ThreadLocal<UserInterface> instance = new ThreadLocal<>();
}
