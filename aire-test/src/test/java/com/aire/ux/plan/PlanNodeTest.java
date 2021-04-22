package com.aire.ux.plan;

import static org.junit.jupiter.api.Assertions.*;

import com.aire.ux.select.css.CssSelectorParserTest.TestCase;
import lombok.val;
import org.junit.jupiter.api.Test;

class PlanNodeTest extends TestCase {

  @Test
  void ensureCollectingSimpleTypeSelectorWorks() {
    val result = parser.parse("hello");
    //    val plan = result.plan();

  }
}
