package com.aire.ux.plan.evaluators;

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
    System.out.println(result);
  }


}