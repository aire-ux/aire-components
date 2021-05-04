package com.aire.ux.plan.evaluators;

import static com.aire.ux.test.Nodes.node;
import static org.junit.jupiter.api.Assertions.*;

import com.aire.ux.plan.EvaluatorFactory;
import com.aire.ux.test.Node;
import lombok.val;
import org.junit.jupiter.api.Test;

class AdjacentSiblingEvaluatorFactoryTest extends EvaluatorFactoryTestCase {

  private Node node;

  @Test
  void ensureSimpleSelectorWorks() {
    node =
        node("ul")
            .children(
                node("li").attribute("fst", "true"),
                node("li").attribute("class", "whatever"),
                node("li"));
    val result = eval("ul > li + li.whatever", node, Node.getAdapter());
    assertEquals(1, result.size());
  }

  @Override
  protected EvaluatorFactory createFactory() {
    return new AdjacentSiblingEvaluatorFactory();
  }
}
