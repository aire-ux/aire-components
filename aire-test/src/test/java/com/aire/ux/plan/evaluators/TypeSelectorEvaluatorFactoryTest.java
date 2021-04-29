package com.aire.ux.plan.evaluators;

import static com.aire.ux.test.Nodes.node;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.aire.ux.plan.DefaultPlanContext;
import com.aire.ux.plan.EvaluatorFactory;
import com.aire.ux.plan.PlanContext;
import com.aire.ux.select.css.CssSelectorParser.ElementSymbol;
import com.aire.ux.select.css.CssSelectorParserTest.TestCase;
import com.aire.ux.test.Node;
import java.util.List;
import lombok.val;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class TypeSelectorEvaluatorFactoryTest extends TestCase {

  private Node tree;
  private PlanContext context;
  private EvaluatorFactory factory;

  @BeforeEach
  protected void setUp() {
    super.setUp();
    context = DefaultPlanContext.getInstance();
    factory = new TypeSelectorEvaluatorFactory();
  }

  @Test
  void ensureSelectingSimpleValueFromSimpleTreeWorks() {
    val selector = parser.parse("div");
    val selectors = selector.find(t -> t.getSymbol() == ElementSymbol.TypeSelector);
    assertEquals(1, selectors.size());
    tree = node("body").child(node("div"));
    System.out.println(tree);
    System.out.println(tree.getChildren());
    val result =
        factory.create(selectors.get(0), context).evaluate(List.of(tree), Node.getAdapter());
    assertEquals(result.size(), 1);
    assertEquals("div", result.get(0).getType());
  }

  @Test
  void ensureSelectingSimpleValueFromPlanWorks() {
    tree = node("body").child(node("div"));
    val selector = parser.parse("div");
    val list = selector.plan(context).evaluate(tree, Node.getAdapter());
    assertEquals(1, list.size());
    assertEquals("div", list.get(0).getType());
  }

  @Test
  void ensureSelectingMultipleDivsInHierarchyWorks() {
    tree = node("body").child(node("div").child(node("div")));
    val selector = parser.parse("div");
    val list = selector.plan(context).evaluate(tree, Node.getAdapter());
    assertEquals(2, list.size());
    assertTrue(list.stream().allMatch(t -> t.getType().equals("div")));
  }
}
