package com.aire.ux.condensation;

import java.util.List;

public interface TypeDescriptor<T> {

  Class<T> getType();

  List<Property<?>> getProperties();
}