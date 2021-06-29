package com.aire.ux.theme.context;

import java.util.Objects;
import javax.annotation.Nonnull;
import javax.annotation.concurrent.NotThreadSafe;

@NotThreadSafe
class AbstractThreadLocalContextHolderStrategy implements ThemeContextHolderStrategy {

  final ThreadLocal<ThemeContext> contextHolder;

  protected AbstractThreadLocalContextHolderStrategy(@Nonnull ThreadLocal<ThemeContext> contextHolder) {
    Objects.requireNonNull(contextHolder);
    this.contextHolder = contextHolder;
  }

  /**
   * clear the current context
   */
  @Override
  public void clearContext() {
    contextHolder.remove();
  }

  /**
   * @return a new theme context
   */
  @Override
  public ThemeContext createThemeContext() {
    return new DefaultThemeContext();
  }

  /**
   * @return the current themecontext, creating a new
   * instance if none exists
   */
  @Override
  public ThemeContext getContext() {
    var result = contextHolder.get();
    if(result == null) {
      result = createThemeContext();
      setContext(result);
    }
    return result;
  }

  /**
   *
   * @param context the non-null theme context to set
   */
  @Override
  public void setContext(@Nonnull ThemeContext context) {
    Objects.requireNonNull(context);
    contextHolder.set(context);
  }
}
