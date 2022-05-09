package io.sunshower.zephyr.ui.i18n;

import java.util.Locale;
import java.util.ResourceBundle;

public interface ResourceBundleResolver {

  ResourceBundle resolve(String name, Locale locale);
}
