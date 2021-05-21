package com.aire.ux.select.scenarios.type;

import static com.aire.ux.test.Nodes.node;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.aire.ux.select.ScenarioTestCase;
import com.aire.ux.test.Node;
import lombok.val;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

/** test off of https://www.w3schools.com/xml/dom_examples.asp (use fully-rendered DOM) */
public class ComplexSelectorsTestCase extends ScenarioTestCase {

  private static Node document;

  @BeforeAll
  static void setUpScenario() {
    document = parse("scenarios/complex.html");
  }

  @ParameterizedTest
  @MethodSource("com.aire.ux.select.scenarios.type.AuxiliaryStateTest#names")
  void ensureAttributesWorks(String name) {
    val t1 = System.currentTimeMillis();
    val results = eval("html > body *.fa:nth-child(n+4), div[class*='3'],body", document);

    val state = Node.getAdapter().stateFor(name);
    for (val result : results) {
      result.setState(state);
    }

    val selectors = eval("* :%s".formatted(state.toSymbol().name()), document);
    val t2 = System.currentTimeMillis();
    System.out.println("Ran: " + (t2 - t1));
    assertEquals(65, selectors.size());
  }

  @Test
  @RepeatedTest(100)
  void ensureUnionWithNthChildSelectorWorks() {
    val t1 = System.currentTimeMillis();
    val results = eval("html > body *.fa:nth-child(n+4), div[class*='3'],body", document);
    val t2 = System.currentTimeMillis();

    System.out.println("Ran: " + (t2 - t1));
    assertEquals(65, results.size());
  }

  @Test
  @RepeatedTest(100)
  void ensureUnionWorksCorrectly() {
    val t1 = System.currentTimeMillis();
    val results = eval("html > body a,div[class]", document);
    val t2 = System.currentTimeMillis();

    System.out.println("Ran: " + (t2 - t1));

    assertEquals(545, results.size());

    assertTrue(
        results.stream()
            .allMatch(
                t ->
                    (t.getType().equals("a")
                        || (t.getType().equals("div") && t.hasAttribute("class")))));
  }

  @Test
  void testDescendentOperator() {
    val nod = node("html").child("body").child("div");
    val results = eval("html div", document);
    assertEquals(93, results.size());
  }

  @Test
  void ensureFirstChildWorksProperly() {
    assertEquals(177, eval(":first-child", document).size());
    assertEquals(36, eval("div:first-child", document).size());
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

  @Test
  void ensureDocumentSelectAllWorks() {
    val results = eval("*", document);
    assertEquals(808, results.size());
  }
}
