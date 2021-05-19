package com.aire.ux.plan;

import com.aire.ux.parsers.ast.AbstractSyntaxTree;
import com.aire.ux.parsers.ast.Symbol;
import com.aire.ux.parsers.ast.SyntaxNode;
import com.aire.ux.select.css.Selector;
import com.aire.ux.select.css.Token;

public interface PlanContext {

  default Selector create(SyntaxNode<Symbol, Token> root) {
    return Selector.create(new AbstractSyntaxTree<>(root));
  }

  /**
   * @param node the node to resolve an evaluatorfactory from
   * @return the evaluator
   * @throws java.util.NoSuchElementException if the node doesn't resolve to a corresponding
   *     evaluator factory
   */
  EvaluatorFactory lookup(SyntaxNode<Symbol, Token> node);
}
