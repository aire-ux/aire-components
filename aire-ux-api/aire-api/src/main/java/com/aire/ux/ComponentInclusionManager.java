package com.aire.ux;

import java.util.Collection;

public interface ComponentInclusionManager {

  Registration disableRoutes(Collection<RouteDefinition> routeDefinitions);

  Registration enableRoutes(Collection<RouteDefinition> routeDefinitions);

  Registration register(ComponentInclusionVoter voter);

  boolean decide(ExtensionDefinition<?> extension);
}
