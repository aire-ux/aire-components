package com.aire.ux.plan.evaluators;

import com.aire.ux.parsers.ast.AbstractSyntaxTree;
import com.aire.ux.parsers.ast.Symbol;
import com.aire.ux.parsers.ast.SyntaxNode;
import com.aire.ux.plan.Evaluator;
import com.aire.ux.plan.EvaluatorFactory;
import com.aire.ux.plan.Plan;
import com.aire.ux.plan.PlanContext;
import com.aire.ux.plan.WorkingSet;
import com.aire.ux.select.css.CssSelectorParser.ElementSymbol;
import com.aire.ux.select.css.Token;
import com.aire.ux.test.NodeAdapter;
import java.util.Collections;
import java.util.Set;
import lombok.val;

public class NegationSelectorEvaluatorFactory implements EvaluatorFactory {

  @Override
  public Set<Symbol> getEvaluationTargets() {
    return Collections.singleton(ElementSymbol.Negation);
  }

  @Override
  public Evaluator create(SyntaxNode<Symbol, Token> node, PlanContext context) {
    return new NegationSelectorEvaluator(node, context);
  }

  private static class NegationSelectorEvaluator extends AbstractHierarchySearchingEvaluator {

    private final Plan plan;
    private final PlanContext context;
    private final AbstractSyntaxTree<Symbol, Token> subroot;

    public NegationSelectorEvaluator(
        SyntaxNode<Symbol, Token> node, PlanContext context) {
      super(node, context);
      this.context = context;
      this.subroot = new AbstractSyntaxTree<Symbol, Token>(node.clearChildren());
      this.plan = context.create(subroot.getRoot()).plan(context);
    }

    @Override
    protected <T> boolean appliesTo(NodeAdapter<T> hom, T n, WorkingSet<T> workingSet) {
      val current = WorkingSet.withExclusions(workingSet);
      current.addAll(workingSet.results());
      val exclusions = plan.evaluate(current, hom);
      return !exclusions.contains(n);
    }

  }
}
