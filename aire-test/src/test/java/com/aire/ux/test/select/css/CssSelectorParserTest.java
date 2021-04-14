package com.aire.ux.test.select.css;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.aire.ux.parsers.ast.AbstractSyntaxTree;
import com.aire.ux.parsers.ast.Symbol;
import com.aire.ux.test.select.css.CssSelectorParser.ElementSymbol;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.platform.suite.api.SelectPackages;
import org.junit.platform.suite.api.Suite;

@Suite
@SelectPackages("com.aire.ux.test.select.css.scenarios.type")
public class CssSelectorParserTest {

  @Test
  void whatever() {}

  public static class TestCase {

    protected CssSelectorParser parser;

    @BeforeEach
    void setUp() {
      parser = new CssSelectorParser();
    }

    public static void expectSymbolCount(
        AbstractSyntaxTree<Symbol, Token> tree, ElementSymbol type, int i) {
      assertEquals(i, tree.reduce(0, (node, n) -> node.getSymbol() == type ? n + 1 : n));
    }
  }
}
