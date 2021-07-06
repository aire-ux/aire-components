package com.aire.ux.theme.decorators;

import com.aire.ux.theme.context.AireThemeManager;
import com.aire.ux.theme.context.AireThemeManager.Registration;
import com.aire.ux.theme.context.ThemeChangeListener.ThemeChangeEventType;
import com.aire.ux.theme.context.ThemeContextHolder;
import com.aire.ux.theme.context.ThemeContextHolder.Strategy;
import io.sunshower.arcus.reflect.Reflect;
import java.lang.reflect.AnnotatedElement;
import java.util.ArrayList;
import java.util.List;
import lombok.val;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.extension.AfterAllCallback;
import org.junit.jupiter.api.extension.BeforeAllCallback;
import org.junit.jupiter.api.extension.Extension;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.ExtensionContext.Namespace;

/** preliminary theme extension */
@Order(10)
public class AireThemeExtension implements Extension, BeforeAllCallback, AfterAllCallback {

  static {
    ThemeContextHolder.setStrategy(Strategy.Global);
  }

  @Override
  @SuppressWarnings("unchecked")
  public void beforeAll(ExtensionContext context) throws Exception {

    context
        .getTestClass()
        .ifPresent(
            type -> {
              val themeDef = type.getAnnotation(TestTheme.class);
              if (themeDef != null) {
                AireThemeManager.setTheme(themeDef.value());
                //
                // ThemeContextHolder.getContext().setTheme(Reflect.instantiate(themeDef.value()));
              }
              registerAll(type, context);
            });
  }

  private void registerAll(AnnotatedElement element, ExtensionContext context) {
    val themes = element.getAnnotation(EnableThemes.class);
    if (themes != null) {
      val registrationStore = getRegistrations(context);
      for (val listenerType : themes.listeners()) {
        val listener = Reflect.instantiate(listenerType);
        val registration =
            AireThemeManager.addEventListener(listener, ThemeChangeEventType.ThemeChanged);
        registrationStore.add(registration);
      }
    }
  }

  @Override
  public void afterAll(ExtensionContext context) throws Exception {
    ThemeContextHolder.restoreDefaults();
    unregisterAll(context);
  }

  private void unregisterAll(ExtensionContext context) {
    val registrationStore = getRegistrations(context);
    for (val registration : registrationStore) {
      registration.remove();
    }
  }

  @SuppressWarnings("unchecked")
  private List<Registration> getRegistrations(ExtensionContext ctx) {
    return (List<Registration>)
        ctx.getStore(Namespace.create(AireThemeExtension.class))
            .getOrComputeIfAbsent("registrations", (k) -> new ArrayList<Registration>());
  }
}
