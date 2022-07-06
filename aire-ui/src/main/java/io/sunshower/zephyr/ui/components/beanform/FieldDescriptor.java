package io.sunshower.zephyr.ui.components.beanform;

import io.sunshower.arcus.condensation.Property;
import io.sunshower.arcus.reflect.Reflect;
import java.lang.annotation.Annotation;
import java.util.Set;

public record FieldDescriptor(String name, Property<?> property) {

  public boolean has(Class<? extends Annotation> type) {
    return property.getMember().isAnnotationPresent(type);
  }
}
