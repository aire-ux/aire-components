package com.aire.ux.ext;

import com.aire.ux.UIExtension;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.HasElement;
import java.util.function.Supplier;
import lombok.Getter;
import lombok.NonNull;

public class ExtensionDefinition {


  private final String path;
  @Getter
  private final Class<? extends HasElement> type;
  private final Supplier<Component> supplier;

  public ExtensionDefinition(
      @NonNull final String path,
      @NonNull Supplier<Component> supplier,
      @NonNull final Class<? extends HasElement> type) {
    this.type = type;
    this.path = path;
    this.supplier = supplier;
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

  public HasElement create() {
    return supplier.get();
  }
}
