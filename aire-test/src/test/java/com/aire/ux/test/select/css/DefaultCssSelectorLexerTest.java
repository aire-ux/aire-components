package com.aire.ux.test.select.css;

import static org.junit.jupiter.api.Assertions.*;

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
