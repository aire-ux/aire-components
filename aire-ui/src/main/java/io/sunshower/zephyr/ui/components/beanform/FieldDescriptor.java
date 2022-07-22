package io.sunshower.zephyr.ui.components.beanform;

import io.sunshower.arcus.condensation.Property;
import java.lang.annotation.Annotation;

public record FieldDescriptor(String name, Property<?> property) {

  public boolean has(Class<? extends Annotation> type) {
    return property.getMember().isAnnotationPresent(type);
  }
}
