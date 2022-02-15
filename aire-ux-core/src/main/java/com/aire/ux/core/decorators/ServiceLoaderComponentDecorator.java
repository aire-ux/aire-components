package com.aire.ux.core.decorators;

import java.util.ArrayList;
import java.util.List;
import java.util.ServiceLoader;
import java.util.ServiceLoader.Provider;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.annotation.Nonnull;

public class ServiceLoaderComponentDecorator extends AbstractCompositeComponentDecorator {

  private final List<? super ComponentDecorator> cache;
  private final Supplier<ClassLoader> classLoaderSupplier;

  public ServiceLoaderComponentDecorator(@Nonnull Supplier<ClassLoader> classloader) {
    super(null);
    this.cache = new ArrayList<>();
    this.classLoaderSupplier = classloader;
  }

  public ServiceLoaderComponentDecorator(ClassLoader classloader) {
    this(() -> classloader);
  }

  @Override
  public void close() {
    cache.clear();
  }

  @Override
  @SuppressWarnings("unchecked")
  protected Stream<? extends ComponentDecorator> delegates() {
    if (cache.isEmpty()) {
      cache.addAll(
          ServiceLoader.load(ComponentDecorator.class, classLoaderSupplier.get()).stream()
              .map(Provider::get)
              .collect(Collectors.toList()));
    }
    return (Stream<? extends ComponentDecorator>) cache.stream();
  }
}
