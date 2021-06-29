package com.aire.ux.theme.decorators;

import com.aire.ux.theme.context.ThemeContextHolder;
import lombok.val;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.extension.AfterAllCallback;
import org.junit.jupiter.api.extension.BeforeAllCallback;
import org.junit.jupiter.api.extension.Extension;
import org.junit.jupiter.api.extension.ExtensionContext;

@Order(10)
public class AireThemeExtension implements Extension, BeforeAllCallback, AfterAllCallback {

  @Override
  public void beforeAll(ExtensionContext context) throws Exception {
    context
        .getTestClass()
        .ifPresent(
            type -> {
              val themeDef = type.getAnnotation(WithTheme.class);
              ThemeContextHolder.setStrategyName(themeDef.strategyClass().getName());
            });
  }

  @Override
  public void afterAll(ExtensionContext context) throws Exception {}
}
