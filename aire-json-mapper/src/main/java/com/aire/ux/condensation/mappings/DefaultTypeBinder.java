package com.aire.ux.condensation.mappings;

import com.aire.ux.condensation.PropertyScanner;
import com.aire.ux.condensation.TypeBinder;

public class DefaultTypeBinder implements TypeBinder {

  private final PropertyScanner scanner;

  public DefaultTypeBinder(PropertyScanner scanner) {
    this.scanner = scanner;
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
}
