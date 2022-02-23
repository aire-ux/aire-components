package com.aire.ux.ext;

import com.aire.ux.UIExtension;
import com.vaadin.flow.component.HasElement;
import lombok.Getter;
import lombok.NonNull;

public class ExtensionDefinition {


  private final String path;
  @Getter
  private final Class<? extends HasElement> type;

  public ExtensionDefinition(
      @NonNull final String path,
      @NonNull final Class<? extends HasElement> type) {
    this.type = type;
    this.path = path;
    check(type);
  }


  public String getPath() {
    return path;
  }

  private void check(Class<? extends HasElement> type) {
    if (!type.isAnnotationPresent(UIExtension.class)) {
      throw new IllegalArgumentException(
          String.format("Error: type '%s' must be annotated with @Host", type));
    }
  }
}
