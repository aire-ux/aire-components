package com.aire.ux.condensation;

public interface TypeBinder {

  <T> T instantiate(Class<T> type);

  PropertyScanner getPropertyScanner();

  <T> TypeDescriptor<T> descriptorFor(Class<T> type);
}
