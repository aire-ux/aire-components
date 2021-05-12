package com.aire.ux.plan.evaluators;

import com.aire.ux.parsers.ast.Symbol;
import com.aire.ux.parsers.ast.SyntaxNode;
import com.aire.ux.plan.Evaluator;
import com.aire.ux.plan.EvaluatorFactory;
import com.aire.ux.plan.PlanContext;
import com.aire.ux.select.css.CssSelectorToken;
import com.aire.ux.select.css.Token;
import com.aire.ux.test.NodeAdapter;
import java.util.LinkedHashSet;
import java.util.Set;
import lombok.val;

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

  static class NthChildSelectorEvaluator implements Evaluator {

    final Evaluator delegate;
    final PlanContext context;
    final SyntaxNode<Symbol, Token> node;

    public NthChildSelectorEvaluator(SyntaxNode<Symbol, Token> node, PlanContext context) {
      this.node = node;
      this.context = context;
      this.delegate = detectDelegate(node);
    }

    @Override
    public <T> Set<T> evaluate(Set<T> workingSet, NodeAdapter<T> hom) {
      return delegate.evaluate(workingSet, hom);
    }

    final Evaluator detectDelegate(SyntaxNode<Symbol, Token> node) {
      if (isScalarNumber(node)) {
        return new ScalarEvaluator(node, context);
      }
      return null;
    }

    private static boolean isScalarNumber(SyntaxNode<Symbol, Token> node) {
      val children = node.getChildren();
      return children.size() == 1
          && children.get(0).getSource().getType() == CssSelectorToken.Numeric;
    }
  }

  static class ScalarEvaluator extends AbstractHierarchySearchingEvaluator {
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
      if(parent == null) {
        return false;
      }
      val children = hom.getChildren(parent);
      return children.indexOf(n) + 1 == offset;
    }
  }
}
