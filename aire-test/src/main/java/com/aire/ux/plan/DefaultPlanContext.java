package com.aire.ux.plan;

import com.aire.ux.parsers.ast.Symbol;
import com.aire.ux.parsers.ast.SyntaxNode;
import com.aire.ux.select.css.Token;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.ServiceLoader;
import lombok.val;

public class DefaultPlanContext implements PlanContext {

  private static final Map<Symbol, EvaluatorFactory> factories;

  static {
    factories = new LinkedHashMap<>();
    for (val service :
        ServiceLoader.load(
            EvaluatorFactory.class, Thread.currentThread().getContextClassLoader())) {
      factories.put(service.getEvaluationTarget(), service);
    }
  }

  public static final PlanContext getInstance() {
    return Holder.instance;
  }

  public void register(EvaluatorFactory factory) {
    factories.put(factory.getEvaluationTarget(), factory);
  }

  @Override
  public EvaluatorFactory lookup(SyntaxNode<Symbol, Token> node) {
    val result = factories.get(node.getSymbol());
    if (result == null) {
      throw new NoSuchElementException(
          "Error: no evaluator factory for symbol '%s'".formatted(node.getSymbol()));
    }
    return result;
  }

  private static class Holder {

    static final PlanContext instance = new DefaultPlanContext();
  }
}
