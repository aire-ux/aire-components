package com.aire.ux.plan.evaluators;

import com.aire.ux.parsers.ast.Symbol;
import com.aire.ux.parsers.ast.SyntaxNode;
import com.aire.ux.plan.Evaluator;
import com.aire.ux.plan.EvaluatorFactory;
import com.aire.ux.plan.PlanContext;
import com.aire.ux.select.css.Token;
import java.util.Collections;
import java.util.Set;

public class ActiveSelectorEvaluatorFactory implements EvaluatorFactory {

  static final Symbol symbol = Symbol.symbol(":active");

  @Override
  public Set<Symbol> getEvaluationTargets() {
    return Collections.singleton(symbol);
  }

  @Override
  public Evaluator create(SyntaxNode<Symbol, Token> node, PlanContext context) {
    return new ScalarSymbolEvaluator(node, context, symbol);
  }
}
