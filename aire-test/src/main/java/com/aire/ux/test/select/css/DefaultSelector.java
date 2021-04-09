package com.aire.ux.test.select.css;

import com.aire.ux.parsers.ast.AbstractSyntaxTree;

public class DefaultSelector implements Selector {
  private final AbstractSyntaxTree tree;

  public DefaultSelector() {
    this.tree = new AbstractSyntaxTree();
  }

}
