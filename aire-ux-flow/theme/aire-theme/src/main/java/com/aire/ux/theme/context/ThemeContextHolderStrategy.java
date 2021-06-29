package com.aire.ux.theme.context;

public interface ThemeContextHolderStrategy {

  void clearContext();

  ThemeContext createThemeContext();

  ThemeContext getContext();

  void setContext(ThemeContext context);
}
