package com.aire.ux.plan.evaluators;

import com.aire.ux.parsers.ast.AbstractSyntaxTree;
import com.aire.ux.parsers.ast.Symbol;
import com.aire.ux.parsers.ast.SyntaxNode;
import com.aire.ux.parsing.core.Token;
import com.aire.ux.plan.Evaluator;
import com.aire.ux.plan.EvaluatorFactory;
import com.aire.ux.plan.PlanContext;
import com.aire.ux.plan.WorkingSet;
import com.aire.ux.test.NodeAdapter;
import java.util.Collections;
import java.util.Set;

public class RootNodeEvaluatorFactory implements EvaluatorFactory {

  @Override
  public Set<Symbol> getEvaluationTargets() {
    return Collections.singleton(AbstractSyntaxTree.ROOT_SYMBOL);
  }

  @Override
  public Evaluator create(SyntaxNode<Symbol, Token> node, PlanContext context) {
    return new RootNodeEvaluator(node, context);
  }

  public static class RootNodeEvaluator implements Evaluator {

    private final PlanContext context;
    private final SyntaxNode<Symbol, Token> node;

    public RootNodeEvaluator(SyntaxNode<Symbol, Token> node, PlanContext context) {
      this.node = node;
      this.context = context;
    }

    @Override
    public <T> WorkingSet<T> evaluate(WorkingSet<T> tree, NodeAdapter<T> hom) {
      return tree;
    }

    @Override
    public String toString() {
      return "<root: select working set.  Cost: 0>";
    }
  }
}
