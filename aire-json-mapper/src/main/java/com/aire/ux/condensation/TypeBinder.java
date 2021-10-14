package com.aire.ux.condensation;

public interface TypeBinder {

  <T> T instantiate(Class<T> type);

  PropertyScanner getPropertyScanner();
}
