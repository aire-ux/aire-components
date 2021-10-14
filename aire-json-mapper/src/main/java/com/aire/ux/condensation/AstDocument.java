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
import java.util.Collection;
import java.util.function.BiFunction;
import lombok.val;

public class AstDocument implements Document {

  static final NodeAdapter<SyntaxNode<Value<?>, Token>> nodeAdapter;

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
        case String:
          readStringInto(result, currentDescriptor, child, child.getValue());
      }
    }


  }

  private <T> void readStringInto(
      T result, TypeDescriptor<T> currentDescriptor,
      SyntaxNode<Value<?>, Token> node, Value<?> value) {
    val name = (String) value.getValue();
    val stringValue = this.<String>valueOf(node.getChild(0));
    val property = currentDescriptor.propertyNamed(Mode.Read, name);
    property.set(result, stringValue);
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
