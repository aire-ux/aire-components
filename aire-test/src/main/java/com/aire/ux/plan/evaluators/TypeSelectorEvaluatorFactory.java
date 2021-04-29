package com.aire.ux.plan.evaluators;

import com.aire.ux.parsers.ast.Symbol;
import com.aire.ux.parsers.ast.SyntaxNode;
import com.aire.ux.plan.Evaluator;
import com.aire.ux.plan.EvaluatorFactory;
import com.aire.ux.plan.PlanContext;
import com.aire.ux.select.css.CssSelectorParser.ElementSymbol;
import com.aire.ux.select.css.Token;
import com.aire.ux.test.NodeAdapter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import lombok.val;

public class TypeSelectorEvaluatorFactory implements EvaluatorFactory {

  @Override
  public Symbol getEvaluationTarget() {
    return ElementSymbol.TypeSelector;
  }

  @Override
  public Evaluator create(SyntaxNode<Symbol, Token> node, PlanContext context) {
    return new TypeSelectorEvaluator(node);
  }

  public static class TypeSelectorEvaluator implements Evaluator {

    final SyntaxNode<Symbol, Token> syntaxNode;

    public TypeSelectorEvaluator(SyntaxNode<Symbol, Token> syntaxNode) {
      this.syntaxNode = syntaxNode;
    }

    public String toString() {
      return "%s".formatted(syntaxNode.getSymbol());
    }

    @Override
    public <T> List<T> evaluate(List<T> workingSet, NodeAdapter<T> hom) {
      val results = new ArrayList<T>();
      return workingSet.stream()
          .flatMap(
              node ->
                  hom
                      .reduce(
                          node,
                          results,
                          (n, rs) -> {
                            if (appliesTo(hom, n)) {
                              rs.add(n);
                            }
                            return rs;
                          })
                      .stream())
          .collect(Collectors.toList());
    }

    private <T> boolean appliesTo(NodeAdapter<T> hom, T n) {
      return hom.getType(n).equals(syntaxNode.getSource().getLexeme());
    }
  }
}
