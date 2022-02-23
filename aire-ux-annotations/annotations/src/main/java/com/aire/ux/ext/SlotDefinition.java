package com.aire.ux.ext;

import com.vaadin.flow.component.HasElement;
import lombok.NonNull;

public class SlotDefinition {

  private final String path;
  private final Property property;

  public SlotDefinition(@NonNull String path, @NonNull Property property) {
    this.path = path;
    this.property = property;
  }

  public void set(HasElement component) {}
}
