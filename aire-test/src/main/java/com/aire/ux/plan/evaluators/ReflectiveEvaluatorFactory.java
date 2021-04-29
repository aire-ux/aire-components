package com.aire.ux.plan.evaluators;

import com.aire.ux.parsers.ast.Symbol;
import com.aire.ux.parsers.ast.SyntaxNode;
import com.aire.ux.plan.Evaluator;
import com.aire.ux.plan.EvaluatorFactory;
import com.aire.ux.plan.PlanContext;
import com.aire.ux.select.css.Token;
import io.sunshower.arcus.reflect.Reflect;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

public class ReflectiveEvaluatorFactory implements EvaluatorFactory {

  private final Symbol evaluationTarget;
  private final Class<? extends Evaluator> evaluatorType;
  private final Constructor<? extends Evaluator> constructor;

  public ReflectiveEvaluatorFactory(
      Symbol evaluationTarget, Class<? extends Evaluator> evaluatorType) {
    this.evaluatorType = evaluatorType;
    this.evaluationTarget = evaluationTarget;
    this.constructor =
        Reflect.findConstructor(evaluatorType, Symbol.class, PlanContext.class)
            .orElseThrow(
                () ->
                    new IllegalArgumentException(
                        "Evaluator must have a public constructor accepting a Symbol and a PlanContext"));
  }

  @Override
  public Symbol getEvaluationTarget() {
    return evaluationTarget;
  }

  @Override
  public Evaluator create(SyntaxNode<Symbol, Token> node, PlanContext context) {
    try {
      return constructor.newInstance(node, context);
    } catch (IllegalAccessException | InstantiationException | InvocationTargetException e) {
      throw new IllegalStateException(e);
    }
  }
}
