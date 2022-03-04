package com.aire.ux;

import com.vaadin.flow.component.Component;

public interface RouteDefinition {

  static RouteDefinition session(Class<? extends Component> component) {
    return new DefaultRouteDefinition(Mode.Session, component);
  }

  static RouteDefinition aire(Class<? extends Component> component) {
    return new DefaultRouteDefinition(Mode.Aire, component);
  }

  static RouteDefinition global(Class<? extends Component> component) {
    return new DefaultRouteDefinition(Mode.Global, component);
  }

  Mode getMode();

  Class<? extends Component> getComponent();

  enum Mode {
    Session,
    Aire,
    Global
  }
}
