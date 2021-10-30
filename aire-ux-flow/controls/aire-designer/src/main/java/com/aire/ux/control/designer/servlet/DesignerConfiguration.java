package com.aire.ux.control.designer.servlet;

import com.aire.ux.condensation.Alias;
import com.aire.ux.condensation.Attribute;
import com.aire.ux.condensation.Element;
import com.aire.ux.condensation.RootElement;
import java.io.InputStream;
import java.io.Serializable;
import java.util.List;
import java.util.ServiceLoader;
import java.util.ServiceLoader.Provider;
import lombok.Getter;
import lombok.val;

/** the designer needs quite a few configurations. This */
@RootElement
public class DesignerConfiguration implements Serializable {

  @Getter
  @Attribute(alias = @Alias(read = "resource-root"))
  private String resourceRoot;

  @Getter
  @Element(alias = @Alias(read = "request-mappings"))
  private List<String> requestMappings;

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

  public ClassLoader getClassLoader() {
    return Thread.currentThread().getContextClassLoader();
  }

  public InputStream getResourceAsStream(String requestURI) {
    if (requestMappings != null) {
      val classLoader = getClassLoader();
      for (val mapping : requestMappings) {
        if (requestURI.startsWith(mapping)) {
          val substr = requestURI.substring(mapping.length());
          return classLoader.getResourceAsStream(resourceRoot + substr);
        }
      }
    }
    return null;
  }
}
