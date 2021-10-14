package com.aire.ux.condensation.mappings;

import com.aire.ux.condensation.Property;
import java.util.Collections;
import java.util.Set;

public class MethodAnnotationPropertyScanningStrategy implements PropertyScanningStrategy {

  @Override
  public <T> Set<Property<?>> scan(Class<T> type) {
    return Collections.emptySet();
  }
}
