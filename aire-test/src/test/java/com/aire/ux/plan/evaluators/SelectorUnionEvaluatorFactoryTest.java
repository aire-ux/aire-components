package com.aire.ux.plan.evaluators;

import static com.aire.ux.test.Nodes.node;
import static org.junit.jupiter.api.Assertions.assertEquals;

import com.aire.ux.plan.EvaluatorFactory;
import lombok.val;
import org.junit.jupiter.api.Test;

class SelectorUnionEvaluatorFactoryTest extends EvaluatorFactoryTestCase {

  @Test
  void ensureSelectorSelectsSimpleUnion() {
    val node =
        node("html").children(node("ul"), node("span").children(node("cool")), node("frapper"));
    val results = eval("ul, span > cool, span", node);
    assertEquals(3, results.size());
    assertContainsTypes(results, "ul", "span", "cool");
  }

  @Override
  protected EvaluatorFactory createFactory() {
    return new SelectorUnionEvaluatorFactory();
  }
}
