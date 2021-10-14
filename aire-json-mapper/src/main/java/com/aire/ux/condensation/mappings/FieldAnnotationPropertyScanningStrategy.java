package com.aire.ux.condensation.mappings;

import com.aire.ux.condensation.Attribute;
import com.aire.ux.condensation.Element;
import com.aire.ux.condensation.Property;
import java.lang.reflect.Field;
import java.util.HashSet;
import java.util.Set;
import lombok.val;

public class FieldAnnotationPropertyScanningStrategy implements
    PropertyScanningStrategy {

  @Override
  public <T> Set<Property<?>> scan(Class<T> type) {
    val fields = type.getDeclaredFields();
    val result = new HashSet<Property<?>>();
    for (val field : fields) {
      boolean isElement = false;
      if (field.isAnnotationPresent(Element.class)) {
        result.add(constructElementProperty(type, field, field.getAnnotation(Element.class)));
        isElement = true;
      }
      if (field.isAnnotationPresent(Attribute.class)) {
        if (isElement) {
          throw new IllegalArgumentException(
              String.format(
                  "Error: field '%s' on class '%s' has both @Element and @Attribute annotations",
                  type, field));
        }
        result.add(constructAttributeProperty(type, field, field.getAnnotation(Element.class)));
      }
    }
    return result;
  }

  private <T> Property<?> constructAttributeProperty(Class<T> type, Field field,
      Element annotation) {
    return new FieldProperty();
  }

  private <T> Property<?> constructElementProperty(Class<T> type, Field field, Element annotation) {
    return new FieldProperty();
  }
}
