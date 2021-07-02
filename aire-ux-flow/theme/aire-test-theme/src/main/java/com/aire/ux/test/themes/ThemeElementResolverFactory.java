package com.aire.ux.test.themes;

import com.aire.ux.Theme;
import com.aire.ux.test.Context;
import com.aire.ux.test.ElementResolver;
import com.aire.ux.test.ElementResolverFactory;
import com.aire.ux.theme.context.AireThemeManager;
import io.sunshower.arcus.reflect.Reflect;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Parameter;

public class ThemeElementResolverFactory implements ElementResolverFactory {

  @Override
  public boolean appliesTo(AnnotatedElement element) {
    if (element.isAnnotationPresent(Context.class) && element instanceof Parameter) {
      return Reflect.isCompatible(Theme.class, ((Parameter) element).getType());
    }
    return false;
  }

  @Override
  public ElementResolver create(AnnotatedElement element) {
    return new ThemeElementResolver();
  }

  private static class ThemeElementResolver implements ElementResolver {

    @Override
    @SuppressWarnings("unchecked")
    public <T> T resolve() {
      return (T) AireThemeManager.getCurrent();
    }
  }
}
