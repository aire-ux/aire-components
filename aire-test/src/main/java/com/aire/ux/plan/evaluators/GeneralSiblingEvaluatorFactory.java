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
import lombok.val;

public class GeneralSiblingEvaluatorFactory implements EvaluatorFactory {

  @Override
  public Symbol getEvaluationTarget() {
    return ElementSymbol.GeneralSiblingSelector;
  }

  @Override
  public Evaluator create(SyntaxNode<Symbol, Token> node, PlanContext context) {
    return new GeneralSiblingEvaluator(node, context);
  }

  private static class GeneralSiblingEvaluator implements Evaluator {

    private final CompositeEvaluator delegate;

    private GeneralSiblingEvaluator(SyntaxNode<Symbol, Token> node, PlanContext context) {
      this.delegate = new CompositeEvaluator(context, node.getChildren());
    }

    @Override
    public String toString() {
      return "~";
    }

    @Override
    public <T> WorkingSet<T> evaluate(WorkingSet<T> workingSet, NodeAdapter<T> hom) {
      val results = WorkingSet.<T>create();
      for (val element : workingSet) {
        val sibling = hom.getSucceedingSiblings(element);
        if (sibling != null) {
          results.addAll(delegate.evaluate(WorkingSet.create(sibling), hom));
        }
      }
      return results;
    }
  }
}
