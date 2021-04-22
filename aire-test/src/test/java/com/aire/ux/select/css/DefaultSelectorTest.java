package com.aire.ux.select.css;

import static org.junit.jupiter.api.Assertions.*;

import com.aire.ux.plan.DefaultPlanContext;
import com.aire.ux.plan.evaluators.SimpleSelectorEvaluatorFactory;
import com.aire.ux.select.css.CssSelectorParserTest.TestCase;
import lombok.val;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class DefaultSelectorTest extends TestCase {

  private Selector selector;
  private DefaultPlanContext context;

  @BeforeEach
  void setUp() {
    super.setUp();
    context = new DefaultPlanContext();
  }

  @Test
  void ensureCollectingSimpleTypeSelectorWorks() {
    context.register(new SimpleSelectorEvaluatorFactory());
    selector = parser.parse("div.first.second");
    for(val node : selector.getSyntaxTree()) {
      System.out.println(node);
    }
  }
}
