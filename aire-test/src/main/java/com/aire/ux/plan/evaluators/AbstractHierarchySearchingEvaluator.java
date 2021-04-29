package com.aire.ux.plan.evaluators;

import com.aire.ux.parsers.ast.Symbol;
import com.aire.ux.parsers.ast.SyntaxNode;
import com.aire.ux.plan.Evaluator;
import com.aire.ux.plan.PlanContext;
import com.aire.ux.select.css.Token;
import com.aire.ux.test.NodeAdapter;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Objects;
import javax.annotation.Nonnull;
import lombok.val;

public abstract class AbstractHierarchySearchingEvaluator implements Evaluator {


  @Nonnull
  protected final PlanContext context;
  @Nonnull
  protected final SyntaxNode<Symbol, Token> node;

  public AbstractHierarchySearchingEvaluator(@Nonnull SyntaxNode<Symbol, Token> node,
      @Nonnull PlanContext context) {
    Objects.requireNonNull(node);
    Objects.requireNonNull(context);
    this.node = node;
    this.context = context;
  }

  @Override
  public <T> List<T> evaluate(List<T> workingSet, NodeAdapter<T> hom) {
    val results = new LinkedHashSet<T>();
    for (val node : workingSet) {
      val result = hom.reduce(node, results, (n, rs) -> {
        if (appliesTo(hom, n)) {
          rs.add(n);
        }
        return rs;
      });
      results.addAll(result);
    }
    return new ArrayList<>(results);
  }


  protected abstract <T> boolean appliesTo(NodeAdapter<T> hom, T n);


  @Override
  public String toString() {
    return "%s[%s]".formatted(getClass().getSimpleName(), node.getSymbol());
  }
}
