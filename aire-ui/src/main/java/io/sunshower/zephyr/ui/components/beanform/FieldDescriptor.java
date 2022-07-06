package io.sunshower.zephyr.ui.components.beanform;

import io.sunshower.arcus.reflect.Reflect;
import java.lang.annotation.Annotation;
import java.util.Set;

public record FieldDescriptor(
    String name,
    String label,
    Set<? extends Annotation> annotations
) {


  public boolean has(Class<? extends Annotation> type) {
    return annotations.stream()
        .anyMatch(annotation -> Reflect.isCompatible(type, annotation.annotationType()));
  }

}
