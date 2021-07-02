package com.aire.ux.theme.context;

import com.aire.ux.Theme;
import java.util.Objects;
import javax.annotation.Nonnull;

final class DefaultThemeContext implements ThemeContext {

  private Theme theme;
  private final ThemeContextHolderStrategy strategy;

  DefaultThemeContext(final ThemeContextHolderStrategy strategy) {
    this.strategy = strategy;
  }

  @Override
  public Theme getTheme() {
    if (theme == null) {
      return strategy.getDefault();
    }
    return theme;
  }

  @Override
  public void setTheme(@Nonnull Theme theme) {
    Objects.requireNonNull(theme);
    this.theme = theme;
  }
}
