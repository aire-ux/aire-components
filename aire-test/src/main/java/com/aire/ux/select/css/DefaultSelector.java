package com.aire.ux.select.css;

import com.aire.ux.parsers.ast.AbstractSyntaxTree;
import com.aire.ux.parsers.ast.Symbol;
import com.aire.ux.parsers.ast.SyntaxNode;
import com.aire.ux.plan.Evaluator;
import com.aire.ux.plan.Plan;
import com.aire.ux.plan.PlanContext;
import com.aire.ux.plan.PlanNode;
import java.util.function.BiFunction;

public class DefaultSelector implements Selector {
  private final AbstractSyntaxTree<Symbol, Token> tree;

  public DefaultSelector() {
    this.tree = new AbstractSyntaxTree<>();
  }

  @Override
  public AbstractSyntaxTree<Symbol, Token> getSyntaxTree() {
    return tree;
  }

  @Override
  public Plan plan(PlanContext context) {
    return tree.reduce(new LinkedPlan(), new PlanBuilder(context));
  }

  static class LinkedPlanNode implements PlanNode {
    final PlanNode next;
    final Evaluator evaluator;

    public LinkedPlanNode(PlanNode next, Evaluator evaluator) {
      this.next = next;
      this.evaluator = evaluator;
    }
  }

  private static class LinkedPlan implements Plan {}

  private static class PlanBuilder
      implements BiFunction<SyntaxNode<Symbol, Token>, LinkedPlan, LinkedPlan> {

    public PlanBuilder(PlanContext context) {}

    @Override
    public LinkedPlan apply(SyntaxNode<Symbol, Token> node, LinkedPlan plan) {
      System.out.println(node);
      return plan;
    }
  }
}
