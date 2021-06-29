package com.aire.ux.theme.decorators;

import com.aire.ux.theme.context.ThemeContextHolder;
import io.sunshower.arcus.reflect.Reflect;
import lombok.val;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.extension.AfterAllCallback;
import org.junit.jupiter.api.extension.BeforeAllCallback;
import org.junit.jupiter.api.extension.Extension;
import org.junit.jupiter.api.extension.ExtensionContext;

/** preliminary theme extension */
@Order(10)
public class AireThemeExtension implements Extension, BeforeAllCallback, AfterAllCallback {

  @Override
  public void beforeAll(ExtensionContext context) throws Exception {
    context
        .getTestClass()
        .ifPresent(
            type -> {
              val themeDef = type.getAnnotation(TestTheme.class);
              ThemeContextHolder.getContext().setTheme(Reflect.instantiate(themeDef.value()));
            });
  }

  @Override
  public void afterAll(ExtensionContext context) throws Exception {
    ThemeContextHolder.restoreDefaults();
  }
}
