package com.aire.features;

import com.aire.ux.RouteDefinition;
import com.aire.ux.UserInterface;
import java.util.Set;

public class RouteDefinitionFeature extends FeatureDescriptor {

  final RouteDefinition routeDefinition;

  public RouteDefinitionFeature(
      RouteDefinition definition, String key, String name, String description, String path) {
    super(key, name, path, description);
    this.routeDefinition = definition;
  }

  public RouteDefinitionFeature(RouteDefinition definition) {
    this(
        definition,
        definition.getComponent().getCanonicalName(),
        definition.getComponent().getCanonicalName(),
        definition.getComponent().getSimpleName(),
        "Route[" + definition.getComponent() + "]");
  }

  @Override
  public void setEnabled(boolean b, UserInterface ui) {
    super.setEnabled(b, ui);
    if (b) {
      ui.getComponentInclusionManager().enableRoutes(Set.of(routeDefinition));
    } else {
      ui.getComponentInclusionManager().disableRoutes(Set.of(routeDefinition));
    }
  }
}
