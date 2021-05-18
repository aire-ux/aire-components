package com.aire.ux.plan.evaluators;

import static com.aire.ux.test.Nodes.node;
import static org.junit.jupiter.api.Assertions.assertEquals;

import com.aire.ux.plan.EvaluatorFactory;
import lombok.val;
import org.junit.jupiter.api.Test;

class NthChildSelectorEvaluatorFactoryTest extends EvaluatorFactoryTestCase {

  @Test
  void ensureSimpleFirstChildSelectorWorks() {
    val node = node("ul").children(
        node("li").attribute("a", "1"),
        node("li").attribute("a", "2"),
        node("li").attribute("a", "3")
    );

    val result = eval(":nth-child(1n+1)", node);
    assertEquals(3, result.size());
  }

  @Test
  void ensureNthChildSelectorWorksForOddValues() {
    val node = node("ul").children(
        node("li").attribute("a", "1"),
        node("li").attribute("a", "2"),
        node("li").attribute("a", "3")
    );

    val result = eval(":nth-child(2n+1)", node);
    assertEquals(2, result.size());
  }

  @Test
  void ensureNegativeIndexesWork() {
    val node = node("ul").children(
        node("li").attribute("a", "1"),
        node("li").attribute("a", "2"),
        node("li").attribute("a", "3")
    );

    val result = eval(":nth-child(-n+3)", node);
    assertEquals(3, result.size());
  }

  @Override
  protected EvaluatorFactory createFactory() {
    return new NthChildSelectorEvaluatorFactory();
  }
}