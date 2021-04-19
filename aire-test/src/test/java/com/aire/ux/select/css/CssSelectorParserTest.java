package com.aire.ux.select.css;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.aire.ux.parsers.ast.AbstractSyntaxTree;
import com.aire.ux.parsers.ast.Symbol;
import com.aire.ux.parsers.ast.SyntaxNode;
import com.aire.ux.select.css.CssSelectorParser.ElementSymbol;
import java.util.function.Predicate;
import lombok.val;
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

    public static void expectSymbolCount(
        AbstractSyntaxTree<Symbol, Token> tree,
        Predicate<SyntaxNode<Symbol, Token>> predicate,
        int i) {
      assertEquals(i, tree.reduce(0, (node, n) -> predicate.test(node) ? n + 1 : n));
    }

    public static void expectSymbolCount(
        AbstractSyntaxTree<Symbol, Token> tree, ElementSymbol type, int i) {
      assertEquals(i, tree.reduce(0, (node, n) -> node.getSymbol() == type ? n + 1 : n));
    }

    public static boolean isCombinator(SyntaxNode<Symbol, Token> node) {
      val t = node.getSymbol();
      if (t instanceof ElementSymbol type) {
        switch (type) {
          case ChildSelector:
          case AdjacentSiblingSelector:
          case GeneralSiblingSelector:
          case DescendantSelector:
            return true;
          default:
            return false;
        }
      }
      return false;
    }

    @BeforeEach
    void setUp() {
      parser = new CssSelectorParser();
    }
  }
}
