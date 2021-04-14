package com.aire.ux.test.select.css;

import com.aire.ux.parsers.ast.AbstractSyntaxTree;
import com.aire.ux.parsers.ast.Symbol;

public interface Selector {
  AbstractSyntaxTree<Symbol, Token> getSyntaxTree();
}
