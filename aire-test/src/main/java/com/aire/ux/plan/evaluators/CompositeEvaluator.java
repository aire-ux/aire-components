package com.aire.ux.plan.evaluators;

import com.aire.ux.parsers.ast.Symbol;
import com.aire.ux.parsers.ast.SyntaxNode;
import com.aire.ux.plan.Evaluator;
import com.aire.ux.plan.PlanContext;
import com.aire.ux.select.css.Token;
import com.aire.ux.test.NodeAdapter;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.val;

public final class CompositeEvaluator implements Evaluator {

  final PlanContext context;
  final List<Evaluator> evaluators;

  public CompositeEvaluator(
      PlanContext context, Collection<? extends SyntaxNode<Symbol, Token>> tokens) {
    this.context = context;
    this.evaluators =
        tokens.stream()
            .map(token -> context.lookup(token).create(token, context))
            .collect(Collectors.toList());
  }

  @Override
  public <T> Set<T> evaluate(Set<T> workingSet, NodeAdapter<T> hom) {
    for (val evaluator : evaluators) {
      workingSet = evaluator.evaluate(workingSet, hom);
    }
    return workingSet;
  }
}
