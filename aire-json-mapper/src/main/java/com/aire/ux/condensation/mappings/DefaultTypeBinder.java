package com.aire.ux.condensation.mappings;

import com.aire.ux.condensation.Discriminator;
import com.aire.ux.condensation.Property;
import com.aire.ux.condensation.Property.Mode;
import com.aire.ux.condensation.PropertyScanner;
import com.aire.ux.condensation.TypeBinder;
import com.aire.ux.condensation.TypeDescriptor;
import com.aire.ux.condensation.json.Value;
import com.aire.ux.condensation.json.Value.Type;
import com.aire.ux.parsing.ast.SyntaxNode;
import com.aire.ux.parsing.core.Token;
import io.sunshower.lang.tuple.Pair;
import java.lang.reflect.Array;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import lombok.SneakyThrows;
import lombok.val;

@SuppressWarnings({"unchecked", "PMD.AvoidDuplicateLiterals"})
public class DefaultTypeBinder implements TypeBinder {

  static final Map<Class<?>, CollectionType> typeMapping;
  private static final Set<Class<?>> WRAPPER_TYPES =
      new HashSet<Class<?>>(
          Arrays.asList(
              Boolean.class,
              Character.class,
              Byte.class,
              Short.class,
              Integer.class,
              Long.class,
              Float.class,
              Double.class,
              Void.class,
              String.class));

  static {
    typeMapping = new HashMap<>();
  }

  private final PropertyScanner scanner;
  private final boolean scanInterfaces;
  private final boolean traverseHierarchy;

  public DefaultTypeBinder(PropertyScanner scanner) {
    this(scanner, true, true);
  }

  public DefaultTypeBinder(
      PropertyScanner scanner, boolean traverseHierarchy, boolean scanInterfaces) {
    this.scanner = scanner;
    this.scanInterfaces = scanInterfaces;
    this.traverseHierarchy = traverseHierarchy;
  }

  public static boolean isWrapperType(Class clazz) {
    return WRAPPER_TYPES.contains(clazz);
  }

  @Override
  @SuppressWarnings("unchecked")
  public <T> T instantiate(Class<T> type) {
    return scanner.getTypeInstantiator().instantiate(type);
  }

  @Override
  public PropertyScanner getPropertyScanner() {
    return scanner;
  }

  @Override
  public <T> TypeDescriptor<T> descriptorFor(Class<T> type) {
    val actualType = actualType(type);
    return scanner.scan((Class) actualType, traverseHierarchy, scanInterfaces);
  }

  private <T, U> Class<U> actualType(Class<T> type) {
    if (type.isArray()) {
      return (Class<U>) type.getComponentType();
    }
    if (Collection.class.isAssignableFrom(type)) {
      val typeParameters = type.getTypeParameters();
      if (typeParameters.length > 0) {
        val argType = typeParameters[0];
        val bounds = argType.getBounds();
        if (bounds.length > 0) {
          return (Class<U>) bounds[0];
        }
      }
    }
    return (Class<U>) type;
  }

  @Override
  public <T> T bind(Class<T> type, SyntaxNode<Value<?>, Token> root) {
    if (type.isArray()) {
      return (T) bindArray(type, root);
    } else {
      val result = instantiate(type);
      bind(type, result, root);
      return result;
    }
  }

  private <T> T bindArray(
      Class<T> type,
      SyntaxNode<Value<?>, Token> root
  ) {
    val componentType = actualType(type);
    val result = Array.newInstance(componentType, root.getChildren().size());
    val collectionType = CollectionType.forType(componentType);
    if (CollectionType.forType(componentType) != null) {
      return (T) readPrimitiveArray(componentType, result, collectionType, root.getChildren());
    }
    throw new IllegalStateException();
  }

  private Object readPrimitiveArray(Class<Object> componentType, Object result,
      CollectionType collectionType, List<SyntaxNode<Value<?>, Token>> children) {

    switch (collectionType) {
      case String:
        return readStringArray(result, children);
      case Float:
        return readFloatArray(result, children);
      case Integer:
        return readIntArray(result, children);
      case Double:
        return readDoubleArray(result, children);
      case Short:
        return readShortArray(result, children);
      case Long:
        return readLongArray(result, children);
    }
    return null;
  }

  private Object readStringArray(Object result, List<SyntaxNode<Value<?>, Token>> children) {
    val array = (String[]) result;
    int i = 0;
    for (val child : children) {
      array[i++] = ((String) valueOf(child));
    }
    return array;
  }

  private Object readLongArray(Object result,
      List<SyntaxNode<Value<?>, Token>> children) {
    val array = (long[]) result;
    int i = 0;
    for (val child : children) {
      array[i++] = ((Double) valueOf(child)).longValue();
    }
    return array;
  }

  private Object readShortArray(Object result,
      List<SyntaxNode<Value<?>, Token>> children) {
    val array = (short[]) result;
    int i = 0;
    for (val child : children) {
      array[i++] = ((Double) valueOf(child)).shortValue();
    }
    return array;
  }

  private Object readDoubleArray(Object result, List<SyntaxNode<Value<?>, Token>> children) {
    val array = (double[]) result;
    int i = 0;
    for (val child : children) {
      array[i++] = valueOf(child);
    }
    return array;
  }

  private Object readIntArray(Object result, List<SyntaxNode<Value<?>, Token>> children) {
    val array = (int[]) result;
    int i = 0;
    for (val child : children) {
      array[i++] = ((Double) valueOf(child)).intValue();
    }
    return array;
  }

  private Object readFloatArray(Object result, List<SyntaxNode<Value<?>, Token>> children) {
    val array = (float[]) result;
    int i = 0;
    for (val child : children) {
      array[i++] = ((Double) valueOf(child)).floatValue();
    }
    return array;
  }

  private <T> void bind(Class<T> type, T result, SyntaxNode<Value<?>, Token> node) {
    val currentDescriptor = descriptorFor(type);
    for (val child : node.getChildren()) {
      switch (typeOf(child)) {
        /** at this point we're at the identifier */
        case String:
          readValue(result, currentDescriptor, child, child.getValue());
          break;
        default:
          throw new UnsupportedOperationException();
      }
    }
  }

  private <T> void readValue(
      T result,
      TypeDescriptor<T> currentDescriptor,
      SyntaxNode<Value<?>, Token> node,
      Value<?> value) {
    val name = (String) value.getValue();
    val child = node.getChild(0);
    val property = currentDescriptor.propertyNamed(Mode.Read, name);
    if (isScalar(child)) {
      property.set(result, property.convert(valueOf(child)));
    }
    if (expectsHomogeneousCollection(property)) {
      if (isArray(child) && !property.isCollection()) {
        val componentType = property.getComponentType();
        property.set(result, readHomogeneousArray(property, componentType, child));
      } else if (property.isCollection()) {
        val collection = readCollection(property, child);
        property.set(result, collection);
      }
    }
    if (isObject(child)) {
      val type = property.getType();
      if (isMap(type)) {
        property.set(result, readMap(type, property, child));
      } else {
        val instance = instantiate(type);
        bind(type, instance, child);
        property.set(result, instance);

      }
    }
  }

  private Map<?, ?> readMap(Class<Object> type,
      Property<?> property, SyntaxNode<Value<?>, Token> node) {
    final Map result;
    if (isInstantiable(property)) {
      result = (Map<?, ?>) instantiate(type);
    } else {
      result = new LinkedHashMap<>();
    }
    val genericType = property.getGenericType();
    if (genericType instanceof ParameterizedType) {
      val mapType = ((ParameterizedType) genericType).getActualTypeArguments()[1];
      for (val child : node.getChildren()) {
        val key = valueOf(child);
        val value = read((Class) mapType, child, null);
        result.put(key, value);
      }
    }
    return result;

  }

  private boolean isMap(Class<Object> type) {
    return Map.class.isAssignableFrom(type);
  }


  @SuppressWarnings({"unchecked", "rawtypes"})
  private <T> T read(Class<T> type, SyntaxNode<Value<?>, Token> node,
      Discriminator snd) {
    Class<? extends T> actualType;
    if (snd != null) {
      actualType = readDiscriminatorType(node, snd);
    } else {
      actualType = type;
    }
    val instance = instantiate(actualType);
    bind((Class) actualType, instance, node);
    return instance;
  }

  @SneakyThrows
  private <T> Class<T> readDiscriminatorType(SyntaxNode<Value<?>, Token> node, Discriminator snd) {
    val field = snd.field();
    for (val child : node.getChildren()) {
      val value = valueOf(child);
      if (field.equals(value)) {
        val typeValue = child.getChild(0);
        return (Class<T>) Class.forName(valueOf(typeValue), true, Thread.currentThread()
            .getContextClassLoader());
      }
    }
    return null;
  }

  private Collection<?> readCollection(Property<?> property, SyntaxNode<Value<?>, Token> child) {
    val isInstantiable = isInstantiable(property);
    final Collection<?> collection;
    if (isInstantiable) {
      collection = instantiate(property.getType());
    } else {
      collection = new ArrayList<>();
    }
    val type = extractTypeFrom(property, child);
    if (isWrapperType(type.fst)) {
      for (val n : child.getChildren()) {
        collection.add(property.convert(valueOf(n)));
      }
    } else {
      for (val n : child.getChildren()) {
        val result = read(type.fst, n, type.snd);
        collection.add(property.convert(result));
      }
    }
    return collection;
  }


  private Object readObjectArray(Property<?> property, Class<Object> componentType,
      SyntaxNode<Value<?>, Token> node) {
    val children = node.getChildren();
    val array = (Object[]) Array.newInstance(componentType, children.size());
    val typePair = extractTypeFrom(property, node);
    int i = 0;
    for (val child : children) {
      array[i++] = read(typePair.fst, child, typePair.snd);
    }
    return array;
  }

  private Pair<Class<?>, Discriminator> extractTypeFrom(Property<?> property,
      SyntaxNode<Value<?>, Token> child) {
    Class<?> actualType = null;
    if (property.getGenericType() instanceof ParameterizedType) {
      val type = (ParameterizedType) property.getGenericType();
      actualType = (Class<?>) type.getActualTypeArguments()[0];
    } else if (property.isArray()) {
      actualType = property.getComponentType();
    }
    if (actualType != null && actualType.isAnnotationPresent(Discriminator.class)) {
      return Pair.of(actualType, actualType.getAnnotation(Discriminator.class));
    } else {
      return Pair.of(actualType, null);
    }

  }

  private boolean isInstantiable(Property<?> property) {
    val collectionType = property.getType();
    return !Modifier.isAbstract(collectionType.getModifiers());
  }

  private boolean isObject(SyntaxNode<Value<?>, Token> child) {
    return child.getValue().getType() == Type.Object;
  }

  private Object readHomogeneousArray(
      Property<?> property, Class<Object> componentType, SyntaxNode<Value<?>, Token> child) {
    if (isWrapperType(componentType)) {
      return readHomogeneousWrapperArray(property, componentType, child);
    } else if (componentType.isPrimitive()) {
      return readPrimitiveArray(property, componentType, child);
    } else {
      return readObjectArray(property, componentType, child);
    }
  }


  @SuppressWarnings("unchecked")
  private Object readPrimitiveArray(
      Property<?> property, Class<Object> componentType, SyntaxNode<Value<?>, Token> child) {
    if (componentType.equals(double.class)) {
      val children = (List<Value<?>>) valueOf(child);
      val result = new double[children.size()];
      int i = 0;
      for (val cvalue : children) {
        result[i++] = property.convert(cvalue.getValue());
      }
      return result;
    }
    if (componentType.equals(float.class)) {
      val children = (List<Value<?>>) valueOf(child);
      val result = new float[children.size()];
      int i = 0;
      for (val cvalue : children) {
        result[i++] = property.convert(((Double) cvalue.getValue()).floatValue());
      }
      return result;
    }
    if (componentType.equals(int.class)) {
      val children = (List<Value<?>>) valueOf(child);
      val result = new int[children.size()];
      int i = 0;
      for (val cvalue : children) {
        result[i++] = property.convert(((Double) cvalue.getValue()).intValue());
      }
      return result;
    }

    if (componentType.equals(short.class)) {
      val children = (List<Value<?>>) valueOf(child);
      val result = new short[children.size()];
      int i = 0;
      for (val cvalue : children) {
        result[i++] = property.convert(((Double) cvalue.getValue()).shortValue());
      }
      return result;
    }

    if (componentType.equals(boolean.class)) {
      val children = (List<Value<?>>) valueOf(child);
      val result = new boolean[children.size()];
      int i = 0;
      for (val cvalue : children) {
        result[i++] = property.convert((Boolean) cvalue.getValue());
      }
      return result;
    }
    return null;
  }

  @SuppressWarnings("unchecked")
  private Object readHomogeneousWrapperArray(
      Property<?> property, Class<Object> componentType, SyntaxNode<Value<?>, Token> child) {

    if (componentType.equals(Double.class)) {
      val children = (List<Value<?>>) valueOf(child);
      val result = new Double[children.size()];
      int i = 0;
      for (val cvalue : children) {
        result[i++] = property.convert(cvalue.getValue());
      }
      return result;
    }
    if (componentType.equals(Float.class)) {
      val children = (List<Value<?>>) valueOf(child);
      val result = new Float[children.size()];
      int i = 0;
      for (val cvalue : children) {
        result[i++] = property.convert(((Double) cvalue.getValue()).floatValue());
      }
      return result;
    }
    if (componentType.equals(Integer.class)) {
      val children = (List<Value<?>>) valueOf(child);
      val result = new Integer[children.size()];
      int i = 0;
      for (val cvalue : children) {
        result[i++] = property.convert(((Double) cvalue.getValue()).intValue());
      }
      return result;
    }

    if (componentType.equals(Short.class)) {
      val children = (List<Value<?>>) valueOf(child);
      val result = new short[children.size()];
      int i = 0;
      for (val cvalue : children) {
        result[i++] = property.convert(((Double) cvalue.getValue()).shortValue());
      }
      return result;
    }

    if (componentType.equals(Boolean.class)) {
      val children = (List<Value<?>>) valueOf(child);
      val result = new Boolean[children.size()];
      int i = 0;
      for (val cvalue : children) {
        result[i++] = property.convert((Boolean) cvalue.getValue());
      }
      return result;
    }

    if (componentType.equals(String.class)) {
      val children = (List<Value<?>>) valueOf(child);
      val result = new String[children.size()];
      int i = 0;
      for (val cvalue : children) {
        result[i++] = property.convert((String) cvalue.getValue());
      }
      return result;
    }
    return null;
  }

  private boolean expectsHomogeneousCollection(Property<?> property) {
    if (property.isCollection() || property.isArray()) {
      if (isWrapperType(property.getComponentType())
          || property.getComponentType().isPrimitive()) {
        return true;
      }

      val genericType = property.getGenericType();
      if (genericType instanceof ParameterizedType) {
        return true;
      }
      if (scanner.getTypeInstantiator().canInstantiate(property.getComponentType())) {
        return true;
      }
    }
    return false;
  }

  private boolean isArray(SyntaxNode<Value<?>, Token> child) {
    return child.getValue().getType() == Type.Array;
  }

  private boolean isScalar(SyntaxNode<Value<?>, Token> child) {
    return child.getValue().isScalar();
  }

  @SuppressWarnings("unchecked")
  private <T> T valueOf(SyntaxNode<Value<?>, Token> child) {
    return (T) child.getValue().getValue();
  }

  private Type typeOf(SyntaxNode<Value<?>, Token> child) {
    return child.getValue().getType();
  }

  enum CollectionType {
    String(String.class),
    Integer(Integer.class, int.class),
    Short(Short.class, short.class),
    Long(Long.class, long.class),
    Double(Double.class, double.class),
    Float(Float.class, float.class);

    CollectionType(Class<?>... types) {
      for (val type : types) {
        typeMapping.put(type, this);
      }
    }

    static CollectionType forType(Class<?> type) {
      return typeMapping.get(type);
    }

  }
}
