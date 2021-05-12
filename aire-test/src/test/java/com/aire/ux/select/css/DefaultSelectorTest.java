package com.aire.ux.select.css;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.aire.ux.plan.DefaultPlanContext;
import com.aire.ux.plan.evaluators.RootNodeEvaluatorFactory.RootNodeEvaluator;
import com.aire.ux.select.css.CssSelectorParserTest.TestCase;
import com.aire.ux.test.Node;
import com.aire.ux.test.Nodes;
import lombok.val;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class DefaultSelectorTest extends TestCase {

  private Selector selector;
  private DefaultPlanContext context;

  @BeforeEach
  protected void setUp() {
    super.setUp();
    context = new DefaultPlanContext();
  }

  @Test
  void ensurePlanToStringIsHelpful() {
//    val result = parser.parse(
//        "span > span[sup $=beans]:nth-child(1) > span.test:nth-child(1).test:nth-child(1).test")
//        .plan(context);
//    val result = parser.parse("div > test").plan(context).analyze(node(""));
//    System.out.println(result);
  }

  @Test
  void ensureCollectingSimpleTypeSelectorWorks() {
    val plan = parser.parse("hello").plan(context);
    val evals = plan.getEvaluators(RootNodeEvaluator.class);
    assertEquals(1, evals.size());
  }

  @Test
  void ensureSelectorCanRetrieveNodesAtFirstLevel() {
    val plan = parser.parse("hello").plan(context);
    val tree = Nodes.node("hello");
    val results = plan.evaluate(tree, Node.getAdapter());
    //    assertEquals(1, results.size());
  }

  @Test
  void ensureCollectingSimpleTypeSelectorDescendantCombinatorClassSelectorWorks() {
    val plan = parser.parse("hello > .world").plan(context);
    val tree = Nodes.node("hello").child(Nodes.node("world").attribute("class", "world"));
  }
}
