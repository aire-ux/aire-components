package com.aire.ux;

import lombok.NonNull;

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
  public void decorate(T value) {

  }

  public ExtensionRegistration getFinalizer() {
    return finalizer;
  }

  public void setFinalizer(@NonNull ExtensionRegistration finalizer) {
    this.finalizer = finalizer;
  }
}
