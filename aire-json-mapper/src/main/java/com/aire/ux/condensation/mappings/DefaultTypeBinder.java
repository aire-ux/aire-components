package com.aire.ux.condensation.mappings;

import com.aire.ux.condensation.Property;
import com.aire.ux.condensation.Property.Mode;
import com.aire.ux.condensation.PropertyScanner;
import com.aire.ux.condensation.TypeBinder;
import com.aire.ux.condensation.TypeDescriptor;
import com.aire.ux.condensation.json.Value;
import com.aire.ux.condensation.json.Value.Type;
import com.aire.ux.parsing.ast.SyntaxNode;
import com.aire.ux.parsing.core.Token;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import lombok.val;

@SuppressWarnings({"unchecked", "PMD.AvoidDuplicateLiterals"})
public class DefaultTypeBinder implements TypeBinder {

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
    return scanner.scan(type, traverseHierarchy, scanInterfaces);
  }

  @Override
  public <T> T bind(Class<T> type, SyntaxNode<Value<?>, Token> root) {
    val result = instantiate(type);
    bind(type, result, root);
    return result;
  }

  private <T> void bind(Class<T> type, T result, SyntaxNode<Value<?>, Token> node) {
    val currentDescriptor = descriptorFor(type);
    for (val child : node.getChildren()) {
      switch (typeOf(child)) {
          /** at this point we're at the identifier */
        case String:
          readScalar(result, currentDescriptor, child, child.getValue());
          break;
        default:
          throw new UnsupportedOperationException();
      }
    }
  }

  private <T> void readScalar(
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
      if (isArray(child)) {
        val componentType = property.getComponentType();
        property.set(result, readHomogeneousArray(property, componentType, child));
      }
    }
    if (isObject(child)) {
      val type = property.getType();
      val instance = instantiate(type);
      bind(type, instance, child);
      property.set(result, instance);
    }
  }

  private boolean isObject(SyntaxNode<Value<?>, Token> child) {
    return child.getValue().getType() == Type.Object;
  }

  private Object readHomogeneousArray(
      Property<?> property, Class<Object> componentType, SyntaxNode<Value<?>, Token> child) {
    if (isWrapperType(componentType)) {
      return readHomogeneousWrapperArray(property, componentType, child);
    } else {
      return readPrimitiveArray(property, componentType, child);
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
      return isWrapperType(property.getComponentType())
          || property.getComponentType().isPrimitive();
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
}
