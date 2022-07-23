package io.sunshower.zephyr.ui.components.beanform;

import io.sunshower.arcus.condensation.Property;
import io.sunshower.arcus.condensation.PropertyScanner;
import io.sunshower.arcus.condensation.TypeDescriptor;
import io.sunshower.arcus.condensation.TypeInstantiator;
import io.sunshower.arcus.condensation.mappings.FieldProperty;
import io.sunshower.arcus.condensation.mappings.ImmutableTypeDescriptor;
import io.sunshower.arcus.condensation.mappings.MutatorProperty;
import io.sunshower.arcus.reflect.Reflect;
import io.sunshower.lang.tuple.Pair;
import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.stream.Stream;
import lombok.val;

public class BeanFormPropertyScanner implements PropertyScanner, TypeInstantiator {

  public BeanFormPropertyScanner() {}

  @Override
  public <T> TypeDescriptor<T> scan(Class<T> type, boolean b, boolean b1) {
    return new ImmutableTypeDescriptor<>(this, type, doScan(type));
  }

  @Override
  public TypeInstantiator getTypeInstantiator() {
    return this;
  }

  @Override
  public <T> boolean canInstantiate(Class<T> aClass) {
    return true;
  }

  @Override
  public <T> T instantiate(Class<T> aClass, Pair<Class<?>, Object>... pairs) {
    return Reflect.instantiate(aClass, pairs);
  }

  private List<Property<?>> doScan(Class<?> type) {
    return Collections.emptyList();
//    return Reflect.collectOverHierarchy(
//            type,
//            (c) -> {
//              try {
//                val propertyDescriptors = Introspector.getBeanInfo(type);
//
//                return Stream.concat(
//                    Arrays.stream(type.getDeclaredFields())
//                        .filter(field -> field.isAnnotationPresent(Field.class))
//                        .map(
//                            field ->
//                                new FieldProperty(
//                                    this, field, type, field.getName(), field.getName())),
//                    Arrays.stream(type.getDeclaredMethods())
//                        .filter(method -> method.isAnnotationPresent(Field.class))
//                        .map(method -> lookup(propertyDescriptors, method)));
//              } catch (IntrospectionException ex) {
//                return Stream.empty();
//              }
//            })
//        .toList();
  }

  private Property<?> lookup(BeanInfo propertyDescriptors, Method method) {
    val properties = propertyDescriptors.getPropertyDescriptors();
    for (val property : properties) {
      if (Objects.equals(property.getReadMethod(), method)
          || Objects.equals(property.getWriteMethod(), method)) {
        return new MutatorProperty(
            this,
            property.getReadMethod(),
            property.getWriteMethod(),
            propertyDescriptors.getBeanDescriptor().getBeanClass(),
            property.getName(),
            property.getName());
      }
    }
    throw new NoSuchElementException(
        "Error: no property associated with '%s' (bean is likely missing a getter or a setter)");
  }
}
