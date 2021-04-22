package com.aire.ux.select.css;

import com.aire.ux.parsers.ast.AbstractSyntaxTree;
import com.aire.ux.parsers.ast.Symbol;
import com.aire.ux.plan.Plan;
import com.aire.ux.plan.PlanContext;

public interface Selector {
  Plan plan(PlanContext context);

  AbstractSyntaxTree<Symbol, Token> getSyntaxTree();
}
