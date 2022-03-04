package com.aire.ux;

import com.vaadin.flow.component.Component;
import lombok.NonNull;

public class DefaultRouteDefinition implements RouteDefinition {

  private final Mode mode;
  private final Class<? extends Component> component;

  public DefaultRouteDefinition(@NonNull Mode mode, @NonNull Class<? extends Component> component) {
    this.mode = mode;
    this.component = component;
  }

  @Override
  public Mode getMode() {
    return mode;
  }

  @Override
  public Class<? extends Component> getComponent() {
    return component;
  }
}
