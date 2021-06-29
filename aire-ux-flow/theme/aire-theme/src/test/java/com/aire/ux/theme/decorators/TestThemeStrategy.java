package com.aire.ux.theme.decorators;

import com.aire.ux.Theme;
import com.aire.ux.ThemeResource;
import com.aire.ux.theme.context.ThemeContext;
import com.aire.ux.theme.context.ThemeContextHolderStrategy;
import com.vaadin.flow.component.HasElement;
import com.vaadin.flow.component.HasStyle;
import java.io.InputStream;
import java.util.Collections;
import java.util.List;
import org.jetbrains.annotations.NotNull;

public class TestThemeStrategy implements ThemeContextHolderStrategy, ThemeContext, Theme {

  @Override
  public void clearContext() {
  }

  @Override
  public ThemeContext createThemeContext() {
    return this;
  }

  @Override
  public ThemeContext getContext() {
    return this;
  }

  @Override
  public void setContext(ThemeContext context) {
  }

  @NotNull
  @Override
  public Theme getTheme() {
    return this;
  }

  @Override
  public void setTheme(@NotNull Theme theme) {
  }

  @Override
  public String getId() {
    return "test-theme";
  }

  @Override
  public InputStream openResource(String path) {
    return getClass().getClassLoader().getResourceAsStream(path);
  }

  @Override
  public List<ThemeResource> getThemeResources() {
    return Collections.emptyList();
  }

  @Override
  public <T extends HasElement> void apply(T value) {
    if (value instanceof HasStyle) {
      ((HasStyle) value).setClassName("test-theme");
    }
  }
}
