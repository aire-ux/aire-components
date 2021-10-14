package com.aire.ux.condensation.mappings;

import com.aire.ux.condensation.AbstractProperty;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import lombok.NonNull;

public class MutatorProperty extends AbstractProperty<Mutator> {


  protected MutatorProperty(
      @NonNull Method getter,
      @NonNull Method setter,
      @NonNull Class<?> host,
      @NonNull String readAlias,
      @NonNull String writeAlias
  ) {
    super(new Mutator(getter, setter), host, readAlias, writeAlias);
  }

  @Override
  public <U> Class<U> getType() {
    return getMember().getReturnType();
  }

  @Override
  public <T> Type getGenericType() {
    return getMember().getGenericType();
  }

  @Override
  public String getMemberReadName() {
    return null;
  }

  @Override
  public <T, U> void set(U host, T value) {

  }

  @Override
  public <T, U> T get(U host) {
    return null;
  }
}
