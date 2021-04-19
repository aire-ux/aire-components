package com.aire.ux.select.css;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class DefaultCssSelectorLexerTest {

  private SelectorLexer lexer;

  @BeforeEach
  void setUp() {
    lexer = new DefaultCssSelectorLexer();
  }

  @Test
  void ensureMatchingElementWorks() {}
}
