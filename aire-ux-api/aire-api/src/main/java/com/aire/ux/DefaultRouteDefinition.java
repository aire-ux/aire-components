package com.aire.ux;

import com.vaadin.flow.component.Component;
import java.util.Collections;
import java.util.List;
import lombok.NonNull;

public class DefaultRouteDefinition implements RouteDefinition {

  private final List<Scope> scopes;
  private final Class<? extends Component> component;

  public DefaultRouteDefinition(@NonNull List<RouteDefinition.Scope> scope,
      @NonNull Class<? extends Component> component) {
    this.scopes = scope;
    this.component = component;
  }

  @Override
  public List<Scope> getScopes() {
    return Collections.unmodifiableList(scopes);
  }

  @Override
  public Class<? extends Component> getComponent() {
    return component;
  }
}
