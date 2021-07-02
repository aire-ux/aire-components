package com.aire.ux.theme.context;

import com.aire.ux.Theme;
import java.util.Objects;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.concurrent.NotThreadSafe;
import lombok.val;

@NotThreadSafe
class AbstractThreadLocalContextHolderStrategy implements ThemeContextHolderStrategy {

  final ThreadLocal<ThemeContext> contextHolder;
  final ThreadLocal<Theme> defaultThemeContextHolder;

  protected AbstractThreadLocalContextHolderStrategy(
      @Nonnull ThreadLocal<ThemeContext> contextHolder,
      @Nonnull ThreadLocal<Theme> defaultThemeContextHolder) {
    Objects.requireNonNull(contextHolder);
    Objects.requireNonNull(defaultThemeContextHolder);
    this.contextHolder = contextHolder;
    this.defaultThemeContextHolder = defaultThemeContextHolder;
  }

  /** clear the current context */
  @Override
  public void clearContext() {
    contextHolder.remove();
  }

  /** @return a new theme context */
  @Override
  public ThemeContext createThemeContext() {
    return new DefaultThemeContext(this);
  }

  /** @return the current themecontext, creating a new instance if none exists */
  @Override
  public ThemeContext getContext() {
    var result = contextHolder.get();
    if (result == null) {
      result = createThemeContext();
      setContext(result);
    }
    return result;
  }

  /** @param context the non-null theme context to set */
  @Override
  public void setContext(@Nonnull ThemeContext context) {
    Objects.requireNonNull(context);
    contextHolder.set(context);
  }

  @Override
  public Theme getDefault() {
    val result = defaultThemeContextHolder.get();
    if (result == null) {
      return EmptyTheme.getInstance();
    }
    return result;
  }

  @Override
  public void setDefault(@Nullable Theme theme) {
    if (theme == null) {
      defaultThemeContextHolder.set(EmptyTheme.getInstance());
    } else {
      defaultThemeContextHolder.set(theme);
    }
  }
}
