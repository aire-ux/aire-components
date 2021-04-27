package com.aire.ux.select.css;

import com.aire.ux.plan.DefaultPlanContext;
import com.aire.ux.select.css.CssSelectorParserTest.TestCase;
import java.util.function.BiFunction;
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
    val plan = parser.parse("hello world").plan(context);
    System.out.println(plan);
  }


}
