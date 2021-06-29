package com.aire.ux.theme.decorators;

import com.aire.ux.core.decorators.ComponentDecorator;
import com.aire.ux.theme.context.ThemeContextHolder;
import com.vaadin.flow.component.HasElement;
import javax.annotation.Nonnull;

public class AireThemeComponentDecorator implements ComponentDecorator {

  public AireThemeComponentDecorator() {
  }

  @Override
  public void decorate(@Nonnull HasElement component) {
    if (ThemeContextHolder.getContext() != null) {
      ThemeContextHolder.getContext().getTheme().apply(component);
    }
  }
}
