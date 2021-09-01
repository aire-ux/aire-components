package com.aire.ux.plan;

import com.aire.ux.parsing.ast.Symbol;
import com.aire.ux.parsing.ast.SyntaxNode;
import com.aire.ux.parsing.core.Token;
import java.util.Set;

public interface EvaluatorFactory {
  Set<Symbol> getEvaluationTargets();

  Evaluator create(SyntaxNode<Symbol, Token> node, PlanContext context);
}
