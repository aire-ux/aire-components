package com.aire.ux.plan.evaluators;

import com.aire.ux.parsers.ast.Symbol;
import com.aire.ux.parsers.ast.SyntaxNode;
import com.aire.ux.parsing.core.Token;
import com.aire.ux.plan.PlanContext;
import com.aire.ux.plan.WorkingSet;
import com.aire.ux.test.NodeAdapter;

public class ScalarSymbolEvaluator extends AbstractHierarchySearchingEvaluator {

  final Symbol symbol;

  public ScalarSymbolEvaluator(SyntaxNode<Symbol, Token> node, PlanContext context, Symbol symbol) {
    super(node, context);
    this.symbol = symbol;
  }

  @Override
  protected <T> boolean appliesTo(NodeAdapter<T> hom, T n, WorkingSet<T> workingSet) {
    return super.appliesTo(hom, n, workingSet) && hom.hasState(n, hom.stateFor(symbol.name()));
  }
}
