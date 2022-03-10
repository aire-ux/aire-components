package com.aire.ux;

import com.vaadin.flow.component.Component;
import java.util.List;
import lombok.val;

public interface RouteDefinition {

  static RouteDefinition session(Class<? extends Component> component) {
    return new DefaultRouteDefinition(List.of(Scope.Session), component);
  }

  static RouteDefinition aire(Class<? extends Component> component) {
    return new DefaultRouteDefinition(List.of(Scope.Aire), component);
  }

  static RouteDefinition global(Class<? extends Component> component) {
    return new DefaultRouteDefinition(List.of(Scope.Global), component);
  }

  static RouteDefinition fromAnnotatedClass(
      Class<? extends Component> type) {

    val extension = type.getAnnotation(RouteExtension.class);
    if (extension == null) {
      throw new IllegalArgumentException(String.format(
          "Error: type '%s' is not annotated with @RouteExtension--please add the annotation to continue",
          type));
    }
    val scopes = List.of(extension.scopes());
    return new DefaultRouteDefinition(scopes, type);
  }

  List<Scope> getScopes();

  Class<? extends Component> getComponent();

  enum Scope {
    Session,
    Aire,
    Global
  }
}
