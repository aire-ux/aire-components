package com.aire.ux.parsers.ast;

import lombok.Getter;
import lombok.val;

public class AbstractSyntaxTree {

  static final Symbol ROOT_SYMBOL =
      new Symbol() {

        public String toString() {
          return "RootSymbol";
        }
      };
  @Getter private final SyntaxNode root;

  public AbstractSyntaxTree(SyntaxNode root) {
    this.root = root;
  }

  public AbstractSyntaxTree() {
    this(new RootSyntaxNode());
  }

  public String toString() {
    val result = new StringBuilder();
    toString(root, result, "", true);
    return result.toString();
  }

  private void toString(SyntaxNode node, StringBuilder out, String indent, boolean last) {

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

  static final class RootSyntaxNode extends AbstractSyntaxNode {

    public RootSyntaxNode() {
      super(ROOT_SYMBOL, null, null, null);
    }

    public String toString() {
      return "RootNode";
    }
  }
}
