package com.aire.ux.plan.evaluators;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.aire.ux.select.ScenarioTestCase;
import lombok.val;
import org.junit.jupiter.api.Test;

class NegationSelectorEvaluatorFactoryTest extends ScenarioTestCase {


  @Test
  void ensureSimpleTypeNegationSelectsCorrectNodes() {
    val node = parseString("""
        <html>
          <body></body>
        </html>
        """);

    val result = eval(":not(body)", node);
    assertEquals(1, result.size());
    assertContainsTypes(result, "html");
  }

  @Test
  void ensureSimpleTypeNegationSelectsCorrectNodes2() {
    val node = parseString("""
        <html>
          <body></body>
        </html>
        """);

    val result = eval(":not(html)", node);
    assertEquals(1, result.size());
    assertContainsTypes(result, "body");
  }

  @Test
  void ensureSelectingNegationOnClassWorks() {

    val node = parseString("""
        <html>
          <body class="coolbeans"></body>
        </html>
        """);

    val result = eval(":not(.coolbeans)", node);
    assertEquals(1, result.size());
    assertContainsTypes(result, "html");
  }


  @Test
  void ensureSelectingNegationOnClassWorks2() {

    val node = parseString("""
        <html class="coolbeans">
          <body></body>
        </html>
        """);

    val result = eval(":not(.coolbeans)", node);
    assertEquals(1, result.size());
    assertContainsTypes(result, "body");
  }

  @Test
  void ensureSelectingDeeplyNestedValuesWorks() {


    val node = parseString("""
        <html class="coolbeans">
          <body>
            <ul>
              <li class="first">
              </li>
              <li class="second">
                <a>one</a>
              </li>
              <li class="third">
                <a>two</a>
              </li>
            </ul>
          </body>
        </html>
        """);



    val result = eval("li:not(.first)", node);
    for(val r : result) {
      System.out.println(r.getType());
    }
//    assertEquals(2, result.size());
//    assertContainsTypes(result, "body");
  }
}