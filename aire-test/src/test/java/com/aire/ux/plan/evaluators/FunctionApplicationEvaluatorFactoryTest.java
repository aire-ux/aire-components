package com.aire.ux.plan.evaluators;

import static com.aire.ux.test.Nodes.node;

import com.aire.ux.plan.EvaluatorFactory;
import lombok.val;
import org.junit.jupiter.api.Test;

class FunctionApplicationEvaluatorFactoryTest extends EvaluatorFactoryTestCase {

  @Test
  void ensureFunctionApplicationWorks() {
    val node = node("html").child(
        node("body")
    );

    val result = eval("html:nth-child(0)", node);

  }


  @Override
  protected EvaluatorFactory createFactory() {
    return new FunctionApplicationEvaluatorFactory();
  }
}