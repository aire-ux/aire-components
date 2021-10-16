package com.aire.ux.condensation;

import io.sunshower.lang.tuple.Pair;
import java.lang.reflect.AccessibleObject;
import java.lang.reflect.ParameterizedType;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.ServiceLoader;
import java.util.function.Function;
import javax.annotation.Nullable;
import lombok.NonNull;
import lombok.val;

@SuppressWarnings({"unchecked", "PMD.AvoidDuplicateLiterals", "PMD.CompareObjectsWithEquals"})
public abstract class AbstractProperty<T extends AccessibleObject> implements Property<T> {

  static final Map<Pair<Class<?>, Class<?>>, Function<?, ?>> defaultConverters;

  static {
    defaultConverters = new HashMap<>();

    val loader =
        ServiceLoader.load(ConverterProvider.class, Thread.currentThread().getContextClassLoader())
            .iterator();
    while (loader.hasNext()) {
      val next = loader.next();
      defaultConverters.put(next.getTypeMapping(), next.getConverter());
    }
  }

  private final T member;
  /** the type of the host-class */
  private final Class<?> host;
  /** the read-alias of this property */
  private final String readAlias;

  private final String writeAlias;
  private final Function<?, T> converter;

  protected AbstractProperty(
      final T member, final Class<?> host, final String readAlias, final String writeAlias) {
    this.host = host;
    this.member = member;
    this.readAlias = readAlias;
    this.writeAlias = writeAlias;
    this.converter = readConverter(host, member);
  }

  protected abstract Function<?,T> readConverter(Class<?> host, T member);

  protected AbstractProperty(
      @NonNull final T member,
      @NonNull final Class<?> host,
      @NonNull final String readAlias,
      @NonNull final String writeAlias,
      @Nullable final Function<?, T> converter) {
    this.host = host;
    this.member = member;
    this.readAlias = readAlias;
    this.writeAlias = writeAlias;
    this.converter = converter;
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
  public <R, S> R convert(S value) {
    if (converter == null) {
      val type = isArray() || isCollection() ? getComponentType() : getType();
      val converter = (Function<S, R>) defaultConverters.get(Pair.of(value.getClass(), type));
      if (converter != null) {
        return converter.apply(value);
      }
    } else {
      return ((Function<S, R>) converter).apply(value);
    }
    return (R) value;
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
