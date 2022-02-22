package com.aire.ux.ext;

import com.aire.ux.core.decorators.ComponentDecorator;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NonNull;

public class ExtensionComponentDecorator implements ComponentDecorator {

  @Getter(AccessLevel.PROTECTED)
  private final ExtensionRegistry registry;


  public ExtensionComponentDecorator(@NonNull final ExtensionRegistry registry) {
    this.registry = registry;
  }


}
