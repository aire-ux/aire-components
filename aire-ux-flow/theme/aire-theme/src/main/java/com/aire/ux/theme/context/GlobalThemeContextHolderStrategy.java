package com.aire.ux.theme.context;

import java.util.Objects;

public class GlobalThemeContextHolderStrategy implements ThemeContextHolderStrategy {

  private static ThemeContext context;

  @Override
  public void clearContext() {
    context = null;
  }

  @Override
  public ThemeContext createThemeContext() {
    return new DefaultThemeContext();
  }

  @Override
  public ThemeContext getContext() {
    if(context == null) {
      context = createThemeContext();
    }
    return context;
  }

  @Override
  public void setContext(ThemeContext context) {
    Objects.requireNonNull(context);
    GlobalThemeContextHolderStrategy.context = context;
  }
}
