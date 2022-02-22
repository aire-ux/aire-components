package com.aire.ux.ext;

import com.aire.ux.UIExtension;
import com.vaadin.flow.component.Component;
import lombok.Getter;
import lombok.NonNull;

public class ExtensionDefinition {


  @Getter
  private final Class<? extends Component> type;

  public ExtensionDefinition(@NonNull final Class<? extends Component> type) {
    this.type = type;
    check(type);
  }

  private void check(Class<? extends Component> type) {
    if (!type.isAnnotationPresent(UIExtension.class)) {
      throw new IllegalArgumentException(
          String.format("Error: type '%s' must be annotated with @Host", type));
    }

  }
}
