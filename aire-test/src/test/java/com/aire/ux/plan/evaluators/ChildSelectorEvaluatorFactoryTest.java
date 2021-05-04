package com.aire.ux.plan.evaluators;

import static com.aire.ux.test.Nodes.node;
import static org.junit.jupiter.api.Assertions.assertEquals;

import com.aire.ux.plan.EvaluatorFactory;
import com.aire.ux.test.Node;
import lombok.val;
import org.junit.jupiter.api.Test;

public class ChildSelectorEvaluatorFactoryTest extends EvaluatorFactoryTestCase {

  Node node;

  @Test
  void ensureChildSelectorWorks() {
    node = node("div").attribute("child", "false").child(node("div").attribute("child", "true"));
    val plan = parser.parse("div > div").plan(context);
    val eval = plan.evaluate(node, Node.getAdapter());
    assertEquals(1, eval.size());
    assertEquals("true", eval.iterator().next().getAttribute("child"));
  }

  @Override
  protected EvaluatorFactory createFactory() {
    return new ChildSelectorCombinatorEvaluatorFactory();
  }
}
