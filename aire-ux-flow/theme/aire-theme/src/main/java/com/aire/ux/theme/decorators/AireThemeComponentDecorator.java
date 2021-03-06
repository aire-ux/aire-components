package com.aire.ux.theme.decorators;

import com.aire.ux.ComponentDecorator;
import com.aire.ux.theme.context.ThemeContextHolder;
import com.vaadin.flow.component.HasElement;
import javax.annotation.Nonnull;
import lombok.val;

public class AireThemeComponentDecorator implements ComponentDecorator {

  public AireThemeComponentDecorator() {}

  @Override
  public void decorate(@Nonnull HasElement component) {
    val theme = ThemeContextHolder.getContext().getTheme();
    theme.apply(component);
  }
}
