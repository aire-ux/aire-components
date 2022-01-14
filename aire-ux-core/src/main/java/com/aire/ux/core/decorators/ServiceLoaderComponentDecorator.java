package com.aire.ux.core.decorators;

import java.util.ServiceLoader;
import java.util.ServiceLoader.Provider;
import java.util.function.Supplier;
import java.util.stream.Stream;

public class ServiceLoaderComponentDecorator extends AbstractCompositeComponentDecorator {

  public ServiceLoaderComponentDecorator(Supplier<ClassLoader> classloader) {
    super(() -> loadComponentDecorators(classloader.get()));
  }

  public ServiceLoaderComponentDecorator(ClassLoader classloader) {
    super(() -> loadComponentDecorators(classloader));
  }

  /**
   * @param classloader the classloader to load component decorators from
   * @return the component decorators
   */
  private static Stream<? extends ComponentDecorator> loadComponentDecorators(
      ClassLoader classloader) {
    return ServiceLoader.load(ComponentDecorator.class, classloader).stream().map(Provider::get);
  }
}
