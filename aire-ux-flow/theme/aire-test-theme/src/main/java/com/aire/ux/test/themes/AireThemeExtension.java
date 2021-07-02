package com.aire.ux.test.themes;

import com.aire.ux.DefaultTheme;
import com.aire.ux.Theme;
import com.aire.ux.theme.context.EmptyTheme;
import com.aire.ux.theme.context.ThemeContextHolder;
import com.aire.ux.theme.context.ThemeContextHolder.Strategy;
import io.sunshower.arcus.reflect.Reflect;
import java.lang.reflect.AnnotatedElement;
import java.util.ArrayDeque;
import java.util.Deque;
import lombok.val;
import org.junit.jupiter.api.extension.AfterAllCallback;
import org.junit.jupiter.api.extension.AfterEachCallback;
import org.junit.jupiter.api.extension.BeforeAllCallback;
import org.junit.jupiter.api.extension.BeforeEachCallback;
import org.junit.jupiter.api.extension.Extension;
import org.junit.jupiter.api.extension.ExtensionContext;

public class AireThemeExtension
    implements Extension,
        BeforeEachCallback,
        BeforeAllCallback,
        AfterEachCallback,
        AfterAllCallback {

  private final Deque<Theme> themes;

  public AireThemeExtension() {
    themes = new ArrayDeque<>();
    ThemeContextHolder.setStrategy(Strategy.ThreadLocal);
  }

  @Override
  public void afterAll(ExtensionContext context) throws Exception {
    context.getTestClass().ifPresent(this::popTheme);
  }

  @Override
  public void afterEach(ExtensionContext context) throws Exception {
    context.getTestMethod().ifPresent(this::popTheme);
  }

  @Override
  public void beforeAll(ExtensionContext context) throws Exception {
    context.getTestClass().ifPresent(this::pushTheme);
  }

  @Override
  public void beforeEach(ExtensionContext context) throws Exception {
    context.getTestMethod().ifPresent(this::pushTheme);
  }

  private void pushTheme(AnnotatedElement element) {
    if (element.isAnnotationPresent(DefaultTheme.class)) {
      val annotation = element.getAnnotation(DefaultTheme.class);
      val type = annotation.value();
      final Theme theme;
      if (type.equals(Theme.class)) {
        theme = EmptyTheme.getInstance();
      } else {
        theme = Reflect.instantiate(type);
      }
      themes.push(theme);
      ThemeContextHolder.getContext().setTheme(theme);
    }
  }

  private void popTheme(AnnotatedElement type) {
    if (type.isAnnotationPresent(DefaultTheme.class)) {
      val theme = themes.pop();
      val currentTheme = themes.peek() != null ? themes.peek() : theme;
      ThemeContextHolder.getContext().setTheme(currentTheme);
    }
  }
}
