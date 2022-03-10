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

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }

    DefaultRouteDefinition that = (DefaultRouteDefinition) o;

    if (getScopes() != null ? !getScopes().equals(that.getScopes()) : that.getScopes() != null) {
      return false;
    }
    return getComponent() != null ? getComponent().equals(that.getComponent())
        : that.getComponent() == null;
  }

  @Override
  public int hashCode() {
    int result = getScopes() != null ? getScopes().hashCode() : 0;
    result = 31 * result + (getComponent() != null ? getComponent().hashCode() : 0);
    return result;
  }
}
