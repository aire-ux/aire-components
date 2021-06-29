package com.aire.ux.theme.context;

import com.aire.ux.Theme;
import java.util.Objects;
import javax.annotation.Nonnull;

final class DefaultThemeContext implements ThemeContext {

  private Theme theme;

  @Override
  public Theme getTheme() {
    return theme;
  }

  @Override
  public void setTheme(@Nonnull Theme theme) {
    Objects.requireNonNull(theme);
    this.theme = theme;

  }
}
