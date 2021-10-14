package com.aire.ux.condensation;

import com.aire.ux.condensation.Property.Mode;
import com.aire.ux.condensation.json.Value;
import com.aire.ux.condensation.json.Value.Type;
import com.aire.ux.condensation.json.Values.ObjectValue;
import com.aire.ux.condensation.selectors.JsonNodeAdapter;
import com.aire.ux.parsing.ast.AbstractSyntaxTree;
import com.aire.ux.parsing.ast.SyntaxNode;
import com.aire.ux.parsing.core.Token;
import com.aire.ux.plan.DefaultPlanContext;
import com.aire.ux.select.css.CssSelectorParser;
import com.aire.ux.test.NodeAdapter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.BiFunction;
import lombok.val;

public class AstDocument implements Document {

  static final NodeAdapter<SyntaxNode<Value<?>, Token>> nodeAdapter;
  private static final Set<Class<?>> WRAPPER_TYPES = new HashSet<Class<?>>(Arrays.asList(
      Boolean.class, Character.class, Byte.class, Short.class, Integer.class, Long.class,
      Float.class, Double.class, Void.class));

  static {
    nodeAdapter = new JsonNodeAdapter();
  }

  private final CssSelectorParser selectorParser;
  private final AbstractSyntaxTree<Value<?>, Token> tree;

  public AstDocument(AbstractSyntaxTree<Value<?>, Token> parse) {
    super();
    this.tree = parse;
    this.selectorParser = new CssSelectorParser();
  }

  public static boolean isWrapperType(Class clazz) {
    return WRAPPER_TYPES.contains(clazz);
  }

  @Override
  public ObjectValue getRoot() {
    return (ObjectValue) tree.getRoot().getValue().getValue();
  }

  @Override
  public <U> U get(String key) {
    return null;
  }

  @Override
  public <U, T extends Value<U>> T getValue(String key) {
    return null;
  }

  @Override
  @SuppressWarnings("unchecked")
  public <T> T select(String selector) {
    val workingSet =
        selectorParser
            .parse(selector)
            .plan(DefaultPlanContext.getInstance())
            .evaluate(tree.getRoot(), nodeAdapter);
    if (workingSet.size() > 0) {
      val next = workingSet.iterator().next();
      return valueFor(next);
    }
    return null;
  }

  @SuppressWarnings("unchecked")
  private <T> T valueFor(SyntaxNode<Value<?>, Token> next) {
    if (next.hasChildren()) {
      val child = next.getChild(0);
      if (child != null) {
        val childValue = child.getValue();
        if (childValue != null) {
          return (T) childValue.getValue();
        }
      }
    } else {
      val childValue = next.getValue();
      if (childValue != null) {
        return (T) childValue.getValue();
      }
    }
    return null;
  }

  @Override
  public Collection<?> selectAll(String selector) {
    val workingSet =
        selectorParser
            .parse(selector)
            .plan(DefaultPlanContext.getInstance())
            .evaluate(tree.getRoot(), nodeAdapter);
    val result = new ArrayList<>(workingSet.size());
    for (val c : workingSet) {
      result.add(valueFor(c));
    }
    return result;
  }

  @Override
  public <T> T read(Class<T> type, TypeBinder strategy) {
    val result = strategy.instantiate(type);
    bind(type, result, tree.getRoot(), strategy);
    return result;
  }

  private <T> void bind(Class<T> type, T result, SyntaxNode<Value<?>, Token> node,
      TypeBinder strategy) {
    val currentDescriptor = strategy.descriptorFor(type);
    for (val child : node.getChildren()) {
      switch (typeOf(child)) {
        /**
         * at this point we're at the identifier
         */
        case String:
          readScalar(result, currentDescriptor, child, child.getValue());
      }
    }


  }

  private <T> void readScalar(
      T result, TypeDescriptor<T> currentDescriptor,
      SyntaxNode<Value<?>, Token> node, Value<?> value) {
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
  }

  private Object readHomogeneousArray(Property<?> property, Class<Object> componentType,
      SyntaxNode<Value<?>, Token> child) {
    if (isWrapperType(componentType)) {
      return readHomogeneousWrapperArray(property, componentType, child);
    } else {
      return readPrimitiveWrapperArray(property, componentType, child);
    }
  }

  @SuppressWarnings("unchecked")
  private Object readPrimitiveWrapperArray(Property<?> property, Class<Object> componentType,
      SyntaxNode<Value<?>, Token> child) {
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
      val result = new float[children.size()];
      int i = 0;
      for (val cvalue : children) {
        result[i++] = property.convert(((Double) cvalue.getValue()).intValue());
      }
      return result;
    }
    return null;
  }

  private Object readHomogeneousWrapperArray(Property<?> property, Class<Object> componentType,
      SyntaxNode<Value<?>, Token> child) {
    return null;
  }

  private boolean expectsHomogeneousCollection(Property<?> property) {
    if (property.isCollection() || property.isArray()) {
      return isWrapperType(property.getComponentType()) || property
          .getComponentType()
          .isPrimitive();
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

  static final class BinderReduction<R> implements BiFunction<SyntaxNode<Value<?>, Token>, R, R> {

    @Override
    public R apply(SyntaxNode<Value<?>, Token> valueTokenSyntaxNode, R r) {
      return null;
    }
  }
}
