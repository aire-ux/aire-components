package com.aire.ux.control.designer.servlet;

import com.aire.ux.condensation.Alias;
import com.aire.ux.condensation.Attribute;
import com.aire.ux.condensation.Element;
import com.aire.ux.condensation.RootElement;
import java.io.InputStream;
import java.io.Serializable;
import java.util.List;
import java.util.NoSuchElementException;
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
    return DesignerConfiguration.class.getClassLoader();
//    return Thread.currentThread().getContextClassLoader();
  }

  public InputStream getResourceAsStream(String requestURI) {
    if (requestMappings == null) {
      return null;
    }
    val classloader = getClassLoader();
    if (classloader == null) {
      throw new IllegalStateException("No context classloader!");
    }
    for (val mapping : requestMappings) {
      if (requestURI.startsWith(mapping)) {
        val substr = requestURI.substring(mapping.length());
        val actualRoot = resourceRoot + substr;
        return classloader.getResourceAsStream(actualRoot);
      }
    }
    throw new NoSuchElementException("No request mapping under: " + requestURI);
  }
}
