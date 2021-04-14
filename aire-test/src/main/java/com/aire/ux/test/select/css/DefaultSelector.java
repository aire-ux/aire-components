package com.aire.ux.test.select.css;

import com.aire.ux.parsers.ast.AbstractSyntaxTree;
import com.aire.ux.parsers.ast.Symbol;

public class DefaultSelector implements Selector {
  private final AbstractSyntaxTree<Symbol, Token> tree;

  public DefaultSelector() {
    this.tree = new AbstractSyntaxTree<>();
  }

  @Override
  public AbstractSyntaxTree<Symbol, Token> getSyntaxTree() {
    return tree;
  }
}
