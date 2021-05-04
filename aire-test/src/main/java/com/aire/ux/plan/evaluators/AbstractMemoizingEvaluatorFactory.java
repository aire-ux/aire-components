package com.aire.ux.plan.evaluators;

import com.aire.ux.parsers.ast.Symbol;
import com.aire.ux.parsers.ast.SyntaxNode;
import com.aire.ux.plan.Evaluator;
import com.aire.ux.plan.EvaluatorFactory;
import com.aire.ux.plan.PlanContext;
import com.aire.ux.select.css.Token;
import java.util.HashMap;
import java.util.Map;

public abstract class AbstractMemoizingEvaluatorFactory implements EvaluatorFactory {

  private final Symbol symbol;
  private Map<SyntaxNode<Symbol, Token>, Evaluator> memoizedEvaluators;

  protected AbstractMemoizingEvaluatorFactory(Symbol symbol) {
    this.symbol = symbol;
    this.memoizedEvaluators = new HashMap<>();
  }

  @Override
  public Symbol getEvaluationTarget() {
    return symbol;
  }

  @Override
  public final Evaluator create(SyntaxNode<Symbol, Token> node, PlanContext context) {
    return memoizedEvaluators.computeIfAbsent(node, t -> createEvaluator(node, context));
  }

  protected abstract Evaluator createEvaluator(SyntaxNode<Symbol, Token> node, PlanContext context);
}
