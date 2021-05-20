package com.aire.ux.plan.evaluators;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.aire.ux.select.ScenarioTestCase;
import lombok.val;
import org.junit.jupiter.api.Test;

class LastChildEvaluatorFactoryTest extends ScenarioTestCase {


  @Test
  void ensureFirstChildWorks() {
    val node = parseString("""
        <html>
          <head></head>
          <body>
          </body>
        </html>
        """);

    val result = eval(":last-child", node);
    assertEquals(1, result.size());
    assertContainsTypes(result, "body");
  }
}