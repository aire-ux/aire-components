package com.aire.ux.ext;

import com.aire.ux.core.decorators.ComponentDecorator;
import com.vaadin.flow.component.HasElement;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NonNull;
import lombok.val;

public class ExtensionComponentDecorator implements ComponentDecorator {

  @Getter(AccessLevel.PROTECTED)
  private final ExtensionRegistry registry;

  public ExtensionComponentDecorator(@NonNull final ExtensionRegistry registry) {
    this.registry = registry;
  }

  @Override
  public void onComponentEntered(@NonNull HasElement component) {
    val type = registry.typeOf(component);
    if (registry.isRegistered(type)) {
      registry.decorate(type, component);
    }
  }

  @Override
  public void onComponentExited(@NonNull HasElement component) {}
}
