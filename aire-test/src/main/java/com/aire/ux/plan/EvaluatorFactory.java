package com.aire.ux.plan;

import com.aire.ux.parsers.ast.Symbol;
import com.aire.ux.parsers.ast.SyntaxNode;
import com.aire.ux.select.css.Token;
import java.util.Set;

public interface EvaluatorFactory {
  Set<Symbol> getEvaluationTargets();

  Evaluator create(SyntaxNode<Symbol, Token> node, PlanContext context);
}
