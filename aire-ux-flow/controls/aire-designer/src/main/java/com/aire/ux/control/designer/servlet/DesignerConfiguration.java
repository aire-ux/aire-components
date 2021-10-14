package com.aire.ux.control.designer.servlet;

import java.util.ServiceLoader;
import java.util.ServiceLoader.Provider;

/** the designer needs quite a few configurations. This */
public class DesignerConfiguration {

  public static DesignerConfiguration load() {
    return ServiceLoader.load(DesignerConfigurationProvider.class).stream()
        .map(Provider::get)
        .findFirst()
        .orElse(getDefaults())
        .load();
  }

  public static DesignerConfigurationProvider getDefaults() {
    return new DefaultDesignerConfigurationProvider();
  }
}
