package com.aire.ux.plan.evaluators;

import com.aire.ux.parsers.ast.Symbol;
import com.aire.ux.parsers.ast.SyntaxNode;
import com.aire.ux.plan.Evaluator;
import com.aire.ux.plan.EvaluatorFactory;
import com.aire.ux.plan.PlanContext;
import com.aire.ux.select.css.CssSelectorParser.ElementSymbol;
import com.aire.ux.select.css.Token;
import com.aire.ux.test.NodeAdapter;
import java.util.LinkedHashSet;
import java.util.Set;
import lombok.val;

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
    public <T> Set<T> evaluate(Set<T> workingSet, NodeAdapter<T> hom) {
      val result = new LinkedHashSet<T>();
      for (val child : workingSet) {
        result.addAll(hom.getChildren(child));
      }
      return result;
    }
  }
}
