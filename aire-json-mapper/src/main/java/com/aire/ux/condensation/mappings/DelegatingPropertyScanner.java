package com.aire.ux.condensation.mappings;

import com.aire.ux.condensation.Property;
import com.aire.ux.condensation.PropertyScanner;
import com.aire.ux.condensation.TypeDescriptor;
import com.aire.ux.condensation.TypeInstantiator;
import io.sunshower.arcus.reflect.Reflect;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import lombok.NonNull;

public class DelegatingPropertyScanner implements PropertyScanner {

  final TypeInstantiator typeInstantiator;

  final List<PropertyScanningStrategy> delegates;

  public DelegatingPropertyScanner(
      @NonNull final TypeInstantiator typeInstantiator, PropertyScanningStrategy... delegates) {
    this.typeInstantiator = typeInstantiator;
    this.delegates = new ArrayList<>(List.of(delegates));
  }

  @Override
  public <T> TypeDescriptor<T> scan(
      Class<T> type, boolean traverseHierarchy, boolean includeInterfaces) {

    final Stream<Property<?>> properties;
    if (traverseHierarchy && includeInterfaces) {
      properties = Reflect.collectOverHierarchy(type,
          t -> delegates.stream().flatMap(delegate -> delegate.scan(t).stream()));
    } else if (traverseHierarchy) {
      properties = Reflect.linearSupertypes(type)
          .flatMap(t -> delegates.stream().flatMap(delegate -> delegate.scan(t).stream()));
    } else if (includeInterfaces) {
      properties = Stream.concat(Stream.of(type), Stream.of(type.getInterfaces()))
          .flatMap(t -> delegates.stream().flatMap(delegate -> delegate.scan(t).stream()));
    } else {
      properties = delegates.stream().flatMap(delegate -> delegate.scan(type).stream());
    }
    return new ImmutableTypeDescriptor<T>(type, properties.collect(Collectors.toList()));
  }

  <T> List<T> concat(Collection<T>... collections) {
    return Stream
        .of(collections)
        .flatMap(Collection::stream).distinct()
        .collect(Collectors.toList());
  }

  @Override
  public TypeInstantiator getTypeInstantiator() {
    return typeInstantiator;
  }
}
