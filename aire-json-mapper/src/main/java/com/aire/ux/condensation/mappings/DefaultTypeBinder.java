package com.aire.ux.condensation.mappings;

import com.aire.ux.condensation.PropertyScanner;
import com.aire.ux.condensation.TypeBinder;
import com.aire.ux.condensation.TypeDescriptor;

public class DefaultTypeBinder implements TypeBinder {

  private final PropertyScanner scanner;
  private final boolean scanInterfaces;
  private final boolean traverseHierarchy;

  public DefaultTypeBinder(PropertyScanner scanner) {
    this(scanner, true, true);
  }

  public DefaultTypeBinder(PropertyScanner scanner, boolean traverseHierarchy,
      boolean scanInterfaces) {
    this.scanner = scanner;
    this.scanInterfaces = scanInterfaces;
    this.traverseHierarchy = traverseHierarchy;
  }

  @Override
  @SuppressWarnings("unchecked")
  public <T> T instantiate(Class<T> type) {
    return scanner.getTypeInstantiator().instantiate(type);
  }

  @Override
  public PropertyScanner getPropertyScanner() {
    return scanner;
  }

  @Override
  public <T> TypeDescriptor<T> descriptorFor(Class<T> type) {
    return scanner.scan(type, traverseHierarchy, scanInterfaces);
  }
}
