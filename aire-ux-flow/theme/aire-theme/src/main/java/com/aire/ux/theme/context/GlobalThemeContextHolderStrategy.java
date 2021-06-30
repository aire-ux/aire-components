package com.aire.ux.theme.context;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.util.Objects;

@SuppressFBWarnings
public class GlobalThemeContextHolderStrategy implements ThemeContextHolderStrategy {

  private static ThemeContext context;

  @Override
  @SuppressWarnings("PMD.NullAssignment")
  public void clearContext() {
    context = null;
  }

  @Override
  public ThemeContext createThemeContext() {
    return new DefaultThemeContext();
  }

  @Override
  public ThemeContext getContext() {
    if (context == null) {
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
