package com.aire.ux.select.scenarios.type;

import static com.aire.ux.test.Nodes.node;
import static org.junit.jupiter.api.Assertions.assertEquals;

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


}
