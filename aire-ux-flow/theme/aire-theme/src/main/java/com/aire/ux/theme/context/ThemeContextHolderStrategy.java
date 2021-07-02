package com.aire.ux.theme.context;

import com.aire.ux.Theme;
import javax.annotation.Nullable;

public interface ThemeContextHolderStrategy {

  void clearContext();

  ThemeContext createThemeContext();

  ThemeContext getContext();

  void setContext(ThemeContext context);

  Theme getDefault();

  void setDefault(@Nullable Theme theme);
}
