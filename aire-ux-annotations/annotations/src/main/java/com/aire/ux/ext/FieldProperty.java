package com.aire.ux.ext;

import java.lang.reflect.Field;

public class FieldProperty implements Property {

  private final Field field;

  public FieldProperty(final Field field) {
    field.trySetAccessible();
    this.field = field;
  }

  @Override
  public void set(Object host, Object value) {
    try {
      this.field.set(host, value);
    } catch (IllegalAccessException e) {
      throw new IllegalStateException(e);
    }
  }

  @Override
  public Object get(Object host) {
    try {
      return field.get(host);
    } catch (IllegalAccessException e) {
      throw new IllegalStateException(e);
    }
  }
}
