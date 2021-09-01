package com.aire.ux.select.css;

import com.aire.ux.parsing.ast.AbstractSyntaxTree;
import com.aire.ux.parsing.ast.Symbol;
import com.aire.ux.parsing.ast.SyntaxNode;
import com.aire.ux.parsing.core.Token;
import com.aire.ux.plan.Plan;
import com.aire.ux.plan.PlanContext;
import java.util.List;
import java.util.function.Predicate;

public interface Selector {

  public static Selector create(AbstractSyntaxTree<Symbol, Token> root) {
    return new DefaultSelector(root);
  }

  /**
   * @param context the context to plan against
   * @return the plan (may be optimized/memoized depending on implementation)
   */
  Plan plan(PlanContext context);

  /** @return the syntax tree that forms the base of this selector */
  AbstractSyntaxTree<Symbol, Token> getSyntaxTree();

  /**
   * @param f the predicate to filter nodes over
   * @return the list of matching nodes (empty if no matches are found)
   */
  List<SyntaxNode<Symbol, Token>> find(Predicate<SyntaxNode<Symbol, Token>> f);
}
