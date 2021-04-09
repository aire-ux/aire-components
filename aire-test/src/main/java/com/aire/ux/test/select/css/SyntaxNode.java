package com.aire.ux.test.select.css;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class SyntaxNode {

  private final Token token;
  private final List<SyntaxNode> children;

  public SyntaxNode(final Token token) {
    this.token = token;
    this.children = new ArrayList<>();
  }

  public void addChild(SyntaxNode syntaxNode) {
    this.children.add(syntaxNode);
  }

  public List<SyntaxNode> getChildren() {
    return Collections.unmodifiableList(children);
  }
}
