package com.aire.ux.plan.evaluators;

import static com.aire.ux.test.Nodes.node;
import static org.junit.jupiter.api.Assertions.assertEquals;

import com.aire.ux.plan.DefaultPlanContext;
import com.aire.ux.plan.EvaluatorFactory;
import com.aire.ux.plan.PlanContext;
import com.aire.ux.select.css.CssSelectorParserTest.TestCase;
import com.aire.ux.test.Node;
import lombok.val;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ClassSelectorEvaluatorFactoryTest extends EvaluatorFactoryTestCase {


  @Test
  void ensureClassSelectorWorksForSingleNode() {
    val ctx = parser.parse(".test").plan(context);
    val node = node("div").attribute("class", "test");
    val result = ctx.evaluate(node, Node.getAdapter());
    assertEquals(1, result.size());
  }

  @Test
  void ensureNodeMatchesMultipleSelectors() {
    val node =
        node("body")
            .children(
                node("div")
                    .attribute("class", "frapper")
                    .child(node("div"))
                    .attribute("class", "frapper"),
                node("porglebee").attribute("class", "frapper"));

    System.out.println(node);

    val plan = parser.parse("div.frapper").plan(context);
    val ctx = plan.evaluate(node, Node.getAdapter());
    assertEquals(1, ctx.size());
  }

  @Test
  void ensureNestedSelectorsAreMatched() {

    val expected =
        """

        <body>
          <div class="frapepr">

          </div>
          <porglebee class="frapper">
          </porglebee>
        """;

    val node =
        node("body")
            .children(
                node("div")
                    .attribute("class", "frapper")
                    .children(
                        node("div").attribute("class", "frapper"),
                        node("hello")
                            .child(
                                node("div")
                                    .attribute("class", "frapper")
                                    .attribute("data-schnorp", "blapper"))),
                node("porglebee").attribute("class", "frapper"));


    val plan = parser.parse("div.frapper").plan(context);
    val ctx = plan.evaluate(node, Node.getAdapter());
    assertEquals(3, ctx.size());
  }

  @Override
  protected EvaluatorFactory createFactory() {
    return new ClassSelectorEvaluatorFactory();
  }
}
