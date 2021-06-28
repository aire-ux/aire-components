package com.aire.ux.plan.evaluators;

import com.aire.ux.parsers.ast.Symbol;
import com.aire.ux.parsers.ast.SyntaxNode;
import com.aire.ux.plan.Evaluator;
import com.aire.ux.plan.EvaluatorFactory;
import com.aire.ux.plan.PlanContext;
import com.aire.ux.plan.WorkingSet;
import com.aire.ux.select.css.Token;
import com.aire.ux.test.NodeAdapter;
import java.util.Collections;
import java.util.Set;

public class NoOpEvaluatorFactory implements EvaluatorFactory {

  private final Symbol symbol;

  protected NoOpEvaluatorFactory(Symbol symbol) {
    this.symbol = symbol;
  }

  @Override
  public Set<Symbol> getEvaluationTargets() {
    return Collections.singleton(symbol);
  }

  @Override
  public Evaluator create(SyntaxNode<Symbol, Token> node, PlanContext context) {
    return Holder.instance;
  }

  static final class Holder {
    static final Evaluator instance = new NoOpEvaluator();
  }

  static final class NoOpEvaluator implements Evaluator {

    @Override
    public <T> WorkingSet<T> evaluate(WorkingSet<T> workingSet, NodeAdapter<T> hom) {
      return workingSet;
    }
  }
}