package com.aire.ux.select.scenarios.type;

import static com.aire.ux.test.Nodes.node;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.aire.ux.select.ScenarioTestCase;
import com.aire.ux.test.Node;
import lombok.val;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

/**
 * test off of https://www.w3schools.com/xml/dom_examples.asp
 * (use fully-rendered DOM)
 */
public class ComplexSelectorsTestCase extends ScenarioTestCase {


  private static Node document;
  @BeforeAll
  static void setUpScenario() {
    document = parse("scenarios/complex.html");
  }

  @Test
  void testDescendentOperator() {
    val nod = node("html").child("body").child("div");
    val results = eval("html div", document);
    assertEquals(93, results.size());
  }


  @Test
  void ensureSelectingAnchorTypesWorks() {
    val results = eval("[class*=w3-bar]", document);
    assertEquals(189, results.size());
  }

  @Test
  void ensureClassSelectorWorks() {
    val results = eval("a.w3-bar-item", document);
    assertEquals(184, results.size());
  }

  @Test
  void ensureSelectorsWorkOnComplexExpressions() {
    val results = eval("a:nth-child(n+3):nth-child(odd)", document);
    assertEquals(200, results.size());
    assertTrue(results.stream().allMatch(n -> n.getType().equals("a")));
  }

  @Test
  void ensureFullDocumentWorks() {
    val results = eval("html > body > * a:nth-child(n+3):nth-child(odd).w3-bar-item", document);
    assertEquals(88, results.size());
  }


}
