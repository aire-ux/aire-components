package com.aire.ux.plan.evaluators;

import static org.junit.jupiter.api.Assertions.*;

import com.aire.ux.select.ScenarioTestCase;
import lombok.val;
import org.junit.jupiter.api.Test;

class FirstChildSelectorEvaluatorFactoryTest extends ScenarioTestCase {

  @Test
  void ensureFirstChildWorks() {
    val node = parseString("""
        <html>
          <head></head>
          <body>
          </body>
        </html>
        """);

    val result = eval(":first-child", node);
    assertEquals(1, result.size());
    assertContainsTypes(result, "head");
  }


}