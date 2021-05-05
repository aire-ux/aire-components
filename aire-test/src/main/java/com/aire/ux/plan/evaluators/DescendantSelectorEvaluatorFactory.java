package com.aire.ux.plan.evaluators;

import com.aire.ux.parsers.ast.Symbol;
import com.aire.ux.parsers.ast.SyntaxNode;
import com.aire.ux.plan.Evaluator;
import com.aire.ux.plan.EvaluatorFactory;
import com.aire.ux.plan.PlanContext;
import com.aire.ux.select.css.CssSelectorParser.ElementSymbol;
import com.aire.ux.select.css.Token;
import com.aire.ux.test.NodeAdapter;
import java.util.ArrayDeque;
import java.util.LinkedHashSet;
import java.util.Set;
import lombok.val;

public class DescendantSelectorEvaluatorFactory implements EvaluatorFactory {

  public DescendantSelectorEvaluatorFactory() {}

  @Override
  public Symbol getEvaluationTarget() {
    return ElementSymbol.DescendantSelector;
  }

  @Override
  public Evaluator create(SyntaxNode<Symbol, Token> node, PlanContext context) {
    return new DescendantSelectorEvaluator();
  }

  static final class DescendantSelectorEvaluator implements Evaluator {

    @Override
    public String toString() {
      return "[descendant]";
    }

    @Override
    public <T> Set<T> evaluate(Set<T> workingSet, NodeAdapter<T> hom) {
      val result = new LinkedHashSet<T>();

      for (val node : workingSet) {
        val stack = new ArrayDeque<T>(hom.getChildren(node));
        while (!stack.isEmpty()) {
          val iter = stack.descendingIterator();
          while (iter.hasNext()) {
            val next = iter.next();
            result.add(next);
            iter.remove();
            stack.addAll(hom.getChildren(next));
          }
        }
      }
      return result;
    }
  }
}
