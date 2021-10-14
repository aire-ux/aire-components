package com.aire.ux.condensation.mappings;

import com.aire.ux.condensation.Property;
import com.aire.ux.condensation.TypeDescriptor;
import java.util.Collections;
import java.util.List;
import lombok.NonNull;

public class ImmutableTypeDescriptor<T> implements TypeDescriptor<T> {

  private final Class<T> type;
  private final List<Property<?>> properties;

  public ImmutableTypeDescriptor(@NonNull Class<T> type, List<Property<?>> properties) {
    this.type = type;
    this.properties = Collections.unmodifiableList(properties);
  }

  @Override
  public Class<T> getType() {
    return type;
  }

  @Override
  public List<Property<?>> getProperties() {
    return properties;
  }
}
