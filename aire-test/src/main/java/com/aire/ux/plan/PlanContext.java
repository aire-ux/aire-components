package com.aire.ux.plan;

import com.aire.ux.parsers.ast.Symbol;
import com.aire.ux.parsers.ast.SyntaxNode;
import com.aire.ux.select.css.Token;

public interface PlanContext {

  EvaluatorFactory lookup(SyntaxNode<Symbol, Token> node);
}
