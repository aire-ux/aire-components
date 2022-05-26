package io.sunshower.zephyr.ui.i18n;

import java.util.Locale;
import java.util.ResourceBundle;

public class AireResourceBundleResolver implements ResourceBundleResolver {

  @Override
  public ResourceBundle resolve(String name, Locale locale) {
    return ResourceBundle.getBundle(
        "i18n." + name, locale, Thread.currentThread().getContextClassLoader());
  }
}
