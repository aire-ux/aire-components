package com.aire.ux.plan.evaluators;

import com.aire.ux.parsers.ast.Symbol;
import com.aire.ux.parsers.ast.SyntaxNode;
import com.aire.ux.plan.Evaluator;
import com.aire.ux.plan.EvaluatorFactory;
import com.aire.ux.plan.Expression;
import com.aire.ux.plan.PlanContext;
import com.aire.ux.select.css.CssSelectorToken;
import com.aire.ux.select.css.Token;
import com.aire.ux.test.NodeAdapter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import lombok.val;
import org.jetbrains.annotations.NotNull;

public class NthChildSelectorEvaluatorFactory implements EvaluatorFactory {

  static final Symbol symbol = Symbol.symbol("nth-child");

  @Override
  public Symbol getEvaluationTarget() {
    return symbol;
  }

  @Override
  public Evaluator create(SyntaxNode<Symbol, Token> node, PlanContext context) {
    return new NthChildSelectorEvaluator(node, context);
  }

  static final class NthChildSelectorEvaluator implements Evaluator {

    final Evaluator delegate;
    final PlanContext context;
    final SyntaxNode<Symbol, Token> node;

    public NthChildSelectorEvaluator(SyntaxNode<Symbol, Token> node, PlanContext context) {
      this.node = node;
      this.context = context;
      this.delegate = detectDelegate(node);
    }

    private static boolean is(SyntaxNode<Symbol, Token> node, String value) {
      val children = node.getChildren();
      if (children.size() == 1) {
        val child = children.get(0);
        return child.getSource().getType() == CssSelectorToken.Identifier
            && value.equalsIgnoreCase(child.getSource().getLexeme());
      }
      return false;
    }

    private static boolean isScalarNumber(SyntaxNode<Symbol, Token> node) {
      val children = node.getChildren();
      return children.size() == 1
          && children.get(0).getSource().getType() == CssSelectorToken.Numeric;
    }

    @Override
    public <T> Set<T> evaluate(Set<T> workingSet, NodeAdapter<T> hom) {
      return delegate.evaluate(workingSet, hom);
    }

    final Evaluator detectDelegate(SyntaxNode<Symbol, Token> node) {
      if (isScalarNumber(node)) {
        return new ScalarEvaluator(node, context);
      }
      if (is(node, "odd")) {
        return new OddEvaluator(node, context);
      }
      if (is(node, "even")) {
        return new EvenEvaluator(node, context);
      }
      return new ExpressionEvaluator(node, context);
    }
  }

  static class ExpressionEvaluator implements Evaluator {

    final Expression expression;

    ExpressionEvaluator(SyntaxNode<Symbol, Token> node, PlanContext context) {
      this.expression = Expression.parse(node.clearChildren());
    }

    @Override
    public <T> Set<T> evaluate(Set<T> workingSet, NodeAdapter<T> hom) {
      val results = new LinkedHashSet<T>();
      for (val node : workingSet) {
        hom.reduce(
            node,
            results,
            (n, rs) -> {
              rs.addAll(collectApplicableChildren(hom, n));
              return rs;
            });
      }
      return results;
    }

    private <T> Collection<? extends T> collectApplicableChildren(NodeAdapter<T> hom, T n) {
      val parent = hom.getParent(n);
      if (parent == null) {
        return Collections.emptySet();
      }
      val siblings = hom.getChildren(parent);

      val results = new ArrayList<T>(siblings.size());
      for (int i = 0; i < siblings.size(); i++) {
        val expr = expression.apply(i) - 1;
        if (expr >= 0 && expr < siblings.size()) {
          results.add(siblings.get(expr));
        }
      }
      return results;
    }
  }

  abstract static class AbstractCountEvaluator extends AbstractHierarchySearchingEvaluator {

    AbstractCountEvaluator(@NotNull SyntaxNode<Symbol, Token> node, @NotNull PlanContext context) {
      super(node, context);
      node.removeChild(0);
    }

    abstract <T> boolean is(List<T> children, T value);

    @Override
    protected <T> boolean appliesTo(NodeAdapter<T> hom, T n) {
      val parent = hom.getParent(n);
      if (parent == null) {
        return false;
      }
      val children = hom.getChildren(parent);
      return is(children, n);
    }
  }

  /**
   * these take advantage of the fact that odd(n + 1) == even(n) and the fact that java collections
   * are zero-indexed while CSS selectors are 1-indexed
   */
  static final class EvenEvaluator extends AbstractCountEvaluator {

    EvenEvaluator(@NotNull SyntaxNode<Symbol, Token> node, @NotNull PlanContext context) {
      super(node, context);
    }

    @Override
    <T> boolean is(List<T> children, T value) {
      val idx = children.indexOf(value);
      return idx >= 0 && idx % 2 == 1;
    }
  }

  static final class OddEvaluator extends AbstractCountEvaluator {

    OddEvaluator(@NotNull SyntaxNode<Symbol, Token> node, @NotNull PlanContext context) {
      super(node, context);
    }

    @Override
    <T> boolean is(List<T> children, T value) {
      val idx = children.indexOf(value);
      return idx >= 0 && idx % 2 == 0;
    }
  }

  static final class FunctionalEvaluator extends AbstractCountEvaluator {

    final int offset;

    FunctionalEvaluator(@NotNull SyntaxNode<Symbol, Token> node, @NotNull PlanContext context) {
      super(node, context);
      this.offset = computeOffset();
    }

    private int computeOffset() {
      return 1;
    }

    @Override
    <T> boolean is(List<T> children, T value) {
      return false;
    }
  }

  static final class ScalarEvaluator extends AbstractHierarchySearchingEvaluator {

    final int offset;

    ScalarEvaluator(SyntaxNode<Symbol, Token> node, PlanContext context) {
      super(node, context);
      this.offset = readOffset();
    }

    private int readOffset() {
      val child = node.removeChild(0);
      return Integer.parseInt(child.getSource().getLexeme());
    }

    @Override
    protected <T> boolean appliesTo(NodeAdapter<T> hom, T n) {
      val parent = hom.getParent(n);
      if (parent == null) {
        return false;
      }
      val children = hom.getChildren(parent);
      return children.indexOf(n) + 1 == offset;
    }
  }
}
