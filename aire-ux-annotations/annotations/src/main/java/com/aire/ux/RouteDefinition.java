package com.aire.ux;

import com.vaadin.flow.component.Component;

public interface RouteDefinition {

  Mode getMode();

  Class<? extends Component> getComponent();

  enum Mode {
    Session,
    Aire, Global
  }


}
