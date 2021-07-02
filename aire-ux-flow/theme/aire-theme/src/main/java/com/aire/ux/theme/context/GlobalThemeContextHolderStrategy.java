package com.aire.ux.theme.context;

import com.aire.ux.Theme;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.util.Objects;

@SuppressFBWarnings
public class GlobalThemeContextHolderStrategy implements ThemeContextHolderStrategy {

  private static Theme defaultTheme;
  private static ThemeContext context;


  @Override
  @SuppressWarnings("PMD.NullAssignment")
  public void clearContext() {
    context = null;
  }

  @Override
  public ThemeContext createThemeContext() {
    return new DefaultThemeContext(this);
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

  @Override
  public Theme getDefault() {
    if (defaultTheme == null) {
      return EmptyTheme.getInstance();
    }
    return defaultTheme;
  }

  @Override
  public void setDefault(Theme theme) {
    if (theme == null) {
      defaultTheme = EmptyTheme.getInstance();
    } else {
      defaultTheme = theme;
    }
  }
}
