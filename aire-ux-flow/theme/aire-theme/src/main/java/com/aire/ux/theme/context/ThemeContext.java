package com.aire.ux.theme.context;

import com.aire.ux.Theme;
import javax.annotation.Nonnull;

public interface ThemeContext {

  /** @return the current theme */
  @Nonnull
  Theme getTheme();

  /**
   * set the current theme
   *
   * @param theme the theme to use
   */
  void setTheme(@Nonnull Theme theme);
}
