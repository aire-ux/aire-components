package com.aire.ux.plan.evaluators;

import com.aire.ux.parsers.ast.Symbol;
import com.aire.ux.parsers.ast.SyntaxNode;
import com.aire.ux.plan.Evaluator;
import com.aire.ux.plan.PlanContext;
import com.aire.ux.select.css.Token;
import com.aire.ux.test.NodeAdapter;
import java.util.LinkedHashSet;
import java.util.Objects;
import java.util.Set;
import javax.annotation.Nonnull;
import lombok.val;

public abstract class AbstractHierarchySearchingEvaluator implements Evaluator {

  @Nonnull protected final PlanContext context;
  @Nonnull protected final SyntaxNode<Symbol, Token> node;

  public AbstractHierarchySearchingEvaluator(
      @Nonnull SyntaxNode<Symbol, Token> node, @Nonnull PlanContext context) {
    Objects.requireNonNull(node);
    Objects.requireNonNull(context);
    this.node = node;
    this.context = context;
  }

  @Override
  public <T> int computeCost(Set<T> workingSet, NodeAdapter<T> hom) {
    int i = 0;
    for(val node : workingSet) {
      hom.reduce(node, i, (n, cost) -> cost + 1);
    }
    return i;
  }

  @Override
  public <T> Set<T> evaluate(Set<T> workingSet, NodeAdapter<T> hom) {
    val results = new LinkedHashSet<T>();
    for (val node : workingSet) {
      hom.reduce(
          node,
          results,
          (n, rs) -> {
            if (appliesTo(hom, n)) {
              rs.add(n);
            }
            return rs;
          });
    }
    return results;
  }

  protected abstract <T> boolean appliesTo(NodeAdapter<T> hom, T n);

  @Override
  public String toString() {
    return "%s[%s]".formatted(getClass().getSimpleName(), node.getSymbol());
  }
}
