package com.aire.ux.select.css;

import com.aire.ux.parsers.ast.AbstractSyntaxTree;
import com.aire.ux.parsers.ast.Symbol;
import com.aire.ux.parsers.ast.SyntaxNode;
import com.aire.ux.plan.Evaluator;
import com.aire.ux.plan.Plan;
import com.aire.ux.plan.PlanContext;
import com.aire.ux.plan.PlanNode;
import java.util.function.BiFunction;
import lombok.val;

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
    return tree.reduce(new LinkedPlan(), new PlanBuilder(context)).freeze();
  }

  static class LinkedPlanNode implements PlanNode {

    private final LinkedPlanNode next;
    private final Evaluator evaluator;


    public LinkedPlanNode(LinkedPlanNode next, Evaluator evaluator) {
      this.next = next;
      this.evaluator = evaluator;
    }
  }

  private static class LinkedPlan implements Plan {

    private LinkedPlanNode head;

    LinkedPlan() {
    }

    LinkedPlan(LinkedPlanNode head) {
      this.head = head;
    }

    static <T> T fold(LinkedPlanNode init, T value, BiFunction<LinkedPlanNode, T, T> f) {
      var result = value;
      for (var c = init; c != null; c = c.next) {
        result = f.apply(c, result);
      }
      return result;
    }

    public Plan freeze() {
      return fold(head, new LinkedPlan(), (node, plan) -> plan.prepend(node.evaluator));
    }


    public String toString() {
      val result = new StringBuilder("[");
      for (var h = head; h != null; h = h.next) {
        result.append(h.evaluator);
        if (h.next != null) {
          result.append(",");
        }
      }
      return result.append("]").toString();
    }


    LinkedPlan prepend(Evaluator evaluator) {
      if (head == null) {
        head = new LinkedPlanNode(null, evaluator);
      } else {
        val result = new LinkedPlanNode(head, evaluator);
        head = result;
      }
      return this;
    }
  }

  private static class PlanBuilder
      implements BiFunction<SyntaxNode<Symbol, Token>, LinkedPlan, LinkedPlan> {

    final PlanContext context;

    public PlanBuilder(PlanContext context) {
      this.context = context;
    }

    @Override
    public LinkedPlan apply(SyntaxNode<Symbol, Token> node, LinkedPlan plan) {
      return plan.prepend(context.lookup(node).create(node, context));
    }
  }
}
