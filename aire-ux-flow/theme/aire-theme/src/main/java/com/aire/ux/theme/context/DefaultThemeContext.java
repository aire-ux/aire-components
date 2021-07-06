package com.aire.ux.theme.context;

import com.aire.ux.Theme;
import java.util.Objects;
import java.util.Optional;
import javax.annotation.Nonnull;
import lombok.val;

final class DefaultThemeContext implements ThemeContext {

  private final ThemeContextHolderStrategy strategy;
  private Theme theme;

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

  @Override
  @SuppressWarnings("PMD.NullAssignment")
  public Optional<Theme> clearTheme() {
    if (theme != null) {
      val result = Optional.of(theme);
      theme = null;
      return result;
    }
    return Optional.empty();
  }
}
