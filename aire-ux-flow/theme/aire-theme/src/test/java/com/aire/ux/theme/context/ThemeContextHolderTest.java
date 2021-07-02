package com.aire.ux.theme.context;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import com.aire.ux.Theme;
import com.aire.ux.theme.context.ThemeContextHolder.Strategy;
import lombok.val;
import org.jetbrains.annotations.Nullable;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

class ThemeContextHolderTest {

  @AfterEach
  void reset() {
    ThemeContextHolder.setStrategy(Strategy.ThreadLocal);
  }

  @Test
  void ensureDefaultContextHolderIsThreadLocal() {
    assertEquals(
        ThemeContextHolder.getStrategy().getClass(), ThreadLocalContextHolderStrategy.class);
  }

  @Test
  void ensureGlobalStrategyCanBeSet() {
    ThemeContextHolder.setStrategy(Strategy.Global);
    assertEquals(
        ThemeContextHolder.getStrategy().getClass(), GlobalThemeContextHolderStrategy.class);
  }

  @Test
  void ensureInheritableThreadLocalStrategyCanBeSet() {
    ThemeContextHolder.setStrategy(Strategy.InheritableThreadLocal);
    assertEquals(
        ThemeContextHolder.getStrategy().getClass(),
        InheritableThreadLocalContextHolderStrategy.class);
  }

  @Test
  void ensureNameCanBeSetToClass() {
    ThemeContextHolder.setStrategyName(TestThemeContextHolderStrategy.class.getName());
    assertEquals(ThemeContextHolder.getStrategy().getClass(), TestThemeContextHolderStrategy.class);
    assertEquals(ThemeContextHolder.getStrategyType(), Strategy.ClassName);
  }

  @Test
  void ensureServiceLoaderWorks() {
    ThemeContextHolder.setStrategy(Strategy.ServiceLoader);
    val strategy = ThemeContextHolder.getStrategy();
    assertEquals(strategy.getClass(), TestThemeContextHolderStrategy.class);
  }

  @ParameterizedTest
  @EnumSource(Strategy.class)
  void ensureThemeContextIsRetrievable(Strategy strategy) {
    if (strategy != Strategy.ClassName) {
      ThemeContextHolder.setStrategy(strategy);
      assertNotNull(ThemeContextHolder.getContext());
    }
  }

  public static class TestThemeContextHolderStrategy implements ThemeContextHolderStrategy {

    ThemeContext context;
    private Theme defaultTheme;

    @Override
    public void clearContext() {
      context = null;
    }

    @Override
    public ThemeContext createThemeContext() {
      return context = new DefaultThemeContext(this);
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
      this.context = context;
    }

    @Override
    public Theme getDefault() {
      return defaultTheme != null ? defaultTheme : EmptyTheme.getInstance();
    }

    @Override
    public void setDefault(@Nullable Theme theme) {
      this.defaultTheme = theme != null ? theme : EmptyTheme.getInstance();
    }
  }
}
