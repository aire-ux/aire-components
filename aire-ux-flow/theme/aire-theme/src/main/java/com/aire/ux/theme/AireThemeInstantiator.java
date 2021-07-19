package com.aire.ux.theme;

import com.aire.ux.DefaultTheme;
import com.aire.ux.Theme;
import com.aire.ux.core.decorators.ComponentDecorator;
import com.aire.ux.core.instantiators.BaseAireInstantiator;
import com.aire.ux.theme.context.ThemeContextHolder;
import com.vaadin.flow.component.HasElement;
import com.vaadin.flow.di.Instantiator;
import io.sunshower.arcus.reflect.Reflect;
import java.util.Objects;
import javax.annotation.Nonnull;
import lombok.val;

public class AireThemeInstantiator extends BaseAireInstantiator {

  public AireThemeInstantiator(
      @Nonnull Instantiator delegate, @Nonnull ComponentDecorator decorator) {
    super(delegate, decorator);
  }

  public AireThemeInstantiator(@Nonnull Instantiator delegate) {
    super(delegate);
  }

  @Override
  protected <T extends HasElement> void decorateRouteTarget(Class<T> componentClass) {
    if (componentClass.isAnnotationPresent(DefaultTheme.class)) {
      val current = ThemeContextHolder.getStrategy().getDefault();
      if (!componentClass.equals(current.getClass())) {
        val annotation = componentClass.getAnnotation(DefaultTheme.class);
        val currentTheme = annotation.value();
        if (!Objects.equals(currentTheme, Theme.class)) {
          ThemeContextHolder.getStrategy().setDefault(Reflect.instantiate(currentTheme));
        }
      }
    }
  }
}
