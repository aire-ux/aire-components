package com.aire.ux.test.select.css;

import static org.junit.jupiter.api.Assertions.*;

import lombok.val;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class CssSelectorParserTest {

  private CssSelectorParser parser;

  @BeforeEach
  void setUp() {
    parser = new CssSelectorParser();
  }

  @Test
  void ensureSelectorCanParseClass() {
    val expr = ".hello";
    val selector = parser.parse(expr);
    //    val root = selector.getRoot();
  }
}
