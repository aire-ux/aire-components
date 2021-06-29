package com.aire.ux.core.decorators;

import java.util.List;
import java.util.ServiceLoader;
import java.util.ServiceLoader.Provider;
import java.util.stream.Collectors;
import javax.annotation.Nonnull;

public class ServiceLoaderComponentDecorator extends CompositeComponentDecorator {

  public ServiceLoaderComponentDecorator(@Nonnull final ClassLoader classloader) {
    super(loadComponentDecorators(classloader));
  }

  /**
   * @param classloader the classloader to load component decorators from
   * @return the component decorators
   */
  private static List<ComponentDecorator> loadComponentDecorators(ClassLoader classloader) {
    return ServiceLoader.load(ComponentDecorator.class, classloader).stream()
        .map(Provider::get)
        .collect(Collectors.toList());
  }
}
