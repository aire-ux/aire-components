package com.aire.ux;

import lombok.NonNull;
import lombok.val;

public class RouteExtensionDefinition<T> implements ExtensionDefinition<T>, Extension<T> {

  final RouteDefinition routeDefinition;
  private ExtensionRegistration finalizer;

  public RouteExtensionDefinition(@NonNull RouteDefinition routeDefinition) {
    this.routeDefinition = routeDefinition;
  }

  public RouteDefinition getRouteDefinition() {
    return routeDefinition;
  }

  @Override
  public String getPath() {
    return routeDefinition.getComponent().getCanonicalName();
  }

  @Override
  public T getValue() {
    throw new UnsupportedOperationException(
        "Error: route extension definitions cannot be instantiated");
  }

  @Override
  public Extension<T> getExtension() {
    return this;
  }

  @Override
  public Selection<T> getSelection() {
    return null;
  }

  @Override
  @SuppressWarnings("unchecked")
  public Class<T> getType() {
    return (Class<T>) routeDefinition.getComponent();
  }

  @Override
  public String getSegment() {
    return getType().getPackageName();
  }

  @Override
  public Class<?> getSource() {
    return getType();
  }

  @Override
  public void decorate(T value) {}

  public ExtensionRegistration getFinalizer() {
    return finalizer;
  }

  public void setFinalizer(@NonNull ExtensionRegistration finalizer) {
    this.finalizer = finalizer;
  }

  @Override
  @SuppressWarnings("unchecked")
  public boolean equals(Object o) {
    if (o == null) {
      return false;
    }
    if (o == this) {
      return true;
    }
    if (getClass().equals(o.getClass())) {
      val definition = (RouteExtensionDefinition) o;
      return definition.getType().equals(getType())
          && definition.getRouteDefinition().getScopes().equals(getRouteDefinition().getScopes());
    }
    return false;
  }

  @Override
  public int hashCode() {
    return 31 * getType().hashCode() + 17 * getRouteDefinition().getScopes().hashCode();
  }
}
