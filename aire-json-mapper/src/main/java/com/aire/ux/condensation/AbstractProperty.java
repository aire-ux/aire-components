package com.aire.ux.condensation;

import java.lang.reflect.AccessibleObject;
import java.lang.reflect.ParameterizedType;
import java.util.Collection;
import lombok.NonNull;
import lombok.val;

public abstract class AbstractProperty<T extends AccessibleObject> implements Property<T> {

  private final T member;
  /** the type of the host-class */
  private final Class<?> host;

  /** the read-alias of this property */
  private final String readAlias;

  private final String writeAlias;

  protected AbstractProperty(
      @NonNull final T member,
      @NonNull final Class<?> host,
      @NonNull final String readAlias,
      @NonNull final String writeAlias) {
    this.host = host;
    this.member = member;
    this.readAlias = readAlias;
    this.writeAlias = writeAlias;
  }

  @Override
  @SuppressWarnings("unchecked")
  public <U> Class<U> getHost() {
    return (Class<U>) host;
  }

  @Override
  public T getMember() {
    return member;
  }

  @Override
  @SuppressWarnings("unchecked")
  public <U> Class<U> getComponentType() {
    if (isArray()) {
      return (Class<U>) getType().getComponentType();
    }
    if (isCollection()) {
      val parameterizedType = (ParameterizedType) getGenericType();
      val typeArgs = parameterizedType.getActualTypeArguments();
      if (typeArgs != null && typeArgs.length > 0) {
        return (Class<U>) typeArgs[0];
      }
    }
    return getType();
  }

  @Override
  public String getReadAlias() {
    return readAlias;
  }

  @Override
  public String getWriteAlias() {
    return writeAlias;
  }

  @Override
  public boolean isArray() {
    return getType().isArray();
  }

  @Override
  public boolean isCollection() {
    return Collection.class.isAssignableFrom(getType());
  }
}
