package com.aire.ux.plan.evaluators;

import static com.aire.ux.test.Nodes.node;
import static org.junit.jupiter.api.Assertions.*;

import com.aire.ux.plan.EvaluatorFactory;
import com.aire.ux.select.css.CssSelectorParser;
import com.aire.ux.test.Node;
import lombok.val;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

class AdjacentSiblingEvaluatorFactoryTest extends EvaluatorFactoryTestCase {

  private Node node;

  @ParameterizedTest
  @ValueSource(strings = {"li+li", "li +li", "li +  li", "li+ li"})
  void ensureBaseCaseWorks(String param) {
    node =
        node("ul")
            .children(
                node("li").attribute("fst", "true"),
                node("li").attribute("class", "whatever"),
                node("li"));
    val result = eval(param, node, Node.getAdapter());
    assertEquals(2, result.size());
  }


  @Test
  void ensureSelectorSequenceWorks() {
    node =
        node("ul")
            .children(
                node("li").attribute("fst", "true"),
                node("li").attribute("class", "whatever").attribute("id", "coolbeans"),
                node("li"));
    val result = eval("ul > li + li#coolbeans.whatever", node, Node.getAdapter());
    assertEquals(1, result.size());
    assertTrue(result.stream().noneMatch(t -> t.hasAttribute("fst")));
    assertTrue(result.stream().allMatch(t -> t.getParent().equals(node)));
  }

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
    assertTrue(result.stream().noneMatch(t -> t.hasAttribute("fst")));
    assertTrue(result.stream().allMatch(t -> t.getParent().equals(node)));
  }


  @Test
  void ensureSimpleSelectorSelectsAllMatchingSiblings() {
    node =
        node("ul")
            .children(
                node("li").attribute("fst", "true"),
                node("li").attribute("class", "whatever"),
                node("li"));
    val result = eval("ul > li + li", node, Node.getAdapter());
    assertEquals(2, result.size());
    assertTrue(result.stream().allMatch(t -> t.getParent().equals(node)));
    assertTrue(result.stream().noneMatch(t -> t.hasAttribute("fst")));
  }


  @Override
  protected EvaluatorFactory createFactory() {
    return new AdjacentSiblingEvaluatorFactory();
  }
}
