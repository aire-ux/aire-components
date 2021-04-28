package com.aire.ux.plan.evaluators;

import com.aire.ux.parsers.ast.Symbol;
import com.aire.ux.parsers.ast.SyntaxNode;
import com.aire.ux.plan.Evaluator;
import com.aire.ux.plan.EvaluatorFactory;
import com.aire.ux.plan.PlanContext;
import com.aire.ux.select.css.CssSelectorParser;
import com.aire.ux.select.css.CssSelectorParser.ElementSymbol;
import com.aire.ux.select.css.Token;

public class SelectorGroupEvaluatorFactory implements EvaluatorFactory {

  @Override
  public Symbol getEvaluationTarget() {
    return ElementSymbol.SelectorGroup;
  }

  @Override
  public Evaluator create(SyntaxNode<Symbol, Token> node, PlanContext context) {
    return new SelectorGroupEvaluator(node, context);
  }

  public static class SelectorGroupEvaluator implements Evaluator {

    public SelectorGroupEvaluator(
        SyntaxNode<Symbol, Token> node, PlanContext context) {
    }
  }
}
