package com.aire.ux.condensation;

public interface PropertyMappingStrategy {

  <T> T instantiate(Class<T> type);

  PropertyScanner getPropertyScanner();
}
