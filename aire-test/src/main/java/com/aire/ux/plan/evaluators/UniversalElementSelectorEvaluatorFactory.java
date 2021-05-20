package com.aire.ux.plan.evaluators;

import com.aire.ux.parsers.ast.Symbol;
import com.aire.ux.parsers.ast.SyntaxNode;
import com.aire.ux.plan.Evaluator;
import com.aire.ux.plan.EvaluatorFactory;
import com.aire.ux.plan.PlanContext;
import com.aire.ux.plan.WorkingSet;
import com.aire.ux.select.css.CssSelectorParser.ElementSymbol;
import com.aire.ux.select.css.Token;
import com.aire.ux.test.NodeAdapter;

public class UniversalElementSelectorEvaluatorFactory implements EvaluatorFactory {

  @Override
  public Symbol getEvaluationTarget() {
    return ElementSymbol.UniversalSelector;
  }

  @Override
  public Evaluator create(SyntaxNode<Symbol, Token> node, PlanContext context) {
    return new UniversalElementSelectorEvaluator(node, context);
  }

  @Override
  public String toString() {
    return getEvaluationTarget().toString();
  }

  private static class UniversalElementSelectorEvaluator
      extends AbstractHierarchySearchingEvaluator {

    private UniversalElementSelectorEvaluator(SyntaxNode<Symbol, Token> node, PlanContext context) {
      super(node, context);
    }

    @Override
    protected <T> boolean appliesTo(NodeAdapter<T> hom, T n, WorkingSet<T> workingSet) {
      return true;
    }

    @Override
    public String toString() {
      return "<universal: select descendant working set.  Cost: N>";
    }
  }
}
