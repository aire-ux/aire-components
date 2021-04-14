package com.aire.ux.parsers.ast;

import java.util.LinkedList;
import java.util.Stack;
import java.util.function.BiFunction;
import lombok.Getter;
import lombok.val;

public class AbstractSyntaxTree<T, U> {

  static final Symbol ROOT_SYMBOL =
      new Symbol() {

        public String toString() {
          return "RootSymbol";
        }
      };

  @Getter private final SyntaxNode<T, U> root;

  public AbstractSyntaxTree(SyntaxNode<T, U> root) {
    this.root = root;
  }

  public AbstractSyntaxTree() {
    this(new RootSyntaxNode<>());
  }

  public String toString() {
    val result = new StringBuilder();
    toString(root, result, "", true);
    return result.toString();
  }

  public <V> V reduce(V initial, BiFunction<SyntaxNode<T, U>, V, V> f) {
    return reduce(Order.Pre, initial, f);
  }

  public <V> V reduce(Order order, V initial, BiFunction<SyntaxNode<T, U>, V, V> f) {
    val struct =
        order == Order.Pre ? new Stack<SyntaxNode<T, U>>() : new LinkedList<SyntaxNode<T, U>>();
    struct.add(root);
    var result = f.apply(root, initial);
    while (!struct.isEmpty()) {
      val iterator = struct.listIterator();
      while (iterator.hasNext()) {
        val next = iterator.next();
        result = f.apply(next, result);
        iterator.remove();
        for (val c : next.getChildren()) {
          iterator.add(c);
        }
      }
    }
    return result;
  }

  private void toString(SyntaxNode<T, U> node, StringBuilder out, String indent, boolean last) {

    if (node == root) {
      out.append(node).append("\n");
    } else {
      out.append(indent).append(last ? "└╴" : "├╴").append(node.toString()).append("\n");
    }

    indent = indent + (last ? "   " : "│  ");
    val iter = node.getChildren().iterator();
    while (iter.hasNext()) {
      val child = iter.next();
      val isLast = !iter.hasNext();
      toString(child, out, indent, isLast);
    }
  }

  public enum Order {
    Pre,
    Post
  }

  static final class RootSyntaxNode<T, U> extends AbstractSyntaxNode<T, U> {

    public RootSyntaxNode() {
      super(ROOT_SYMBOL, null, null, null);
    }

    public String toString() {
      return "RootNode";
    }
  }
}
