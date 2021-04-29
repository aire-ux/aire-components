package com.aire.ux.plan.evaluators;

import com.aire.ux.parsers.ast.Symbol;
import com.aire.ux.parsers.ast.SyntaxNode;
import com.aire.ux.plan.Evaluator;
import com.aire.ux.plan.EvaluatorFactory;
import com.aire.ux.plan.PlanContext;
import com.aire.ux.select.css.CssSelectorParser.ElementSymbol;
import com.aire.ux.select.css.Token;
import com.aire.ux.test.NodeAdapter;
import java.util.List;

public class ChildSelectorCombinatorEvaluatorFactory implements EvaluatorFactory {

  @Override
  public Symbol getEvaluationTarget() {
    return ElementSymbol.ChildSelector;
  }

  @Override
  public Evaluator create(SyntaxNode<Symbol, Token> node, PlanContext context) {
    return new ChildSelectorCombinatorEvaluator(node, context);
  }

  private static class ChildSelectorCombinatorEvaluator implements Evaluator {

    public ChildSelectorCombinatorEvaluator(SyntaxNode<Symbol, Token> node, PlanContext context) {}

    @Override
    public <T> List<T> evaluate(List<T> workingSet, NodeAdapter<T> hom) {
      return workingSet;
    }
  }
}
