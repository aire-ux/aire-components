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

  private static class UniversalElementSelectorEvaluator implements Evaluator {
    private UniversalElementSelectorEvaluator(
        SyntaxNode<Symbol, Token> node, PlanContext context) {}

    @Override
    public String toString() {
      return "*";
    }

    @Override
    public <T> Set<T> evaluate(Set<T> workingSet, NodeAdapter<T> hom) {
      val result = new LinkedHashSet<T>();
      for (val element : workingSet) {
        hom.reduce(
            element,
            result,
            (t, u) -> {
              u.add(t);
              return u;
            });
      }
      return result;
    }
  }
}
