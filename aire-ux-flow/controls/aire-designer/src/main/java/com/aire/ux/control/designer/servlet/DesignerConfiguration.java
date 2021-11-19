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
import java.util.concurrent.atomic.AtomicReference;
import lombok.Getter;
import lombok.val;

/** the designer needs quite a few configurations. This */
@RootElement
public class DesignerConfiguration implements Serializable {

  static final AtomicReference<DesignerConfiguration> configuration;

  static {
    configuration = new AtomicReference<>();
  }

  @Getter
  @Attribute(alias = @Alias(read = "resource-root"))
  private String resourceRoot;

  @Getter
  @Element(alias = @Alias(read = "request-mappings"))
  private List<String> requestMappings;

  @Getter
  @Element(alias = @Alias(read = "base-path"))
  private String basePath;

  public static DesignerConfiguration load() {
    val result =
        ServiceLoader.load(DesignerConfigurationProvider.class).stream()
            .map(Provider::get)
            .findFirst()
            .orElse(getDefaults())
            .load();
    configuration.set(result);
    return result;
  }

  public static DesignerConfiguration getInstance() {
    val result = configuration.get();
    if (result == null) {
      return load();
    }
    return result;
  }

  public static DesignerConfigurationProvider getDefaults() {
    return new DefaultDesignerConfigurationProvider();
  }

  public ClassLoader getClassLoader() {
    //    return DesignerConfiguration.class.getClassLoader();
    return Thread.currentThread().getContextClassLoader();
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
        val result = classloader.getResourceAsStream(actualRoot);
        if (result != null) {
          return result;
        }
      }
    }
    throw new NoSuchElementException("No request mapping under: " + requestURI);
  }
}
