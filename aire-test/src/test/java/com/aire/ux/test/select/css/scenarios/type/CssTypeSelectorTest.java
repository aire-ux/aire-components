package com.aire.ux.test.select.css.scenarios.type;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.aire.ux.test.select.css.CssSelectorParser.ElementSymbol;
import com.aire.ux.test.select.css.CssSelectorParserTest.TestCase;
import lombok.val;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

public class CssTypeSelectorTest extends TestCase {

  @ParameterizedTest
  @ValueSource(strings = {"a,b", "hello,world", "a, b", "a,     b"})
  @DisplayName("we must be able to parse a simple unioned combinator")
  void ensureSimpleSingleTypeSelectorWorks(String value) {
    val parse = parser.parse(value);
    expectSymbolCount(parse.getSyntaxTree(), ElementSymbol.Union, 1);
    expectSymbolCount(parse.getSyntaxTree(), ElementSymbol.TypeSelector, 2);
  }

  @ParameterizedTest
  @ValueSource(strings = {"hello > world", "hello>world", "a>b", "a+b", "a b", "a ~ b", "a ~   b"})
  void ensureSimpleCombinatorWorks(String value) {
    val parse = parser.parse(value);
    expectSymbolCount(parse.getSyntaxTree(), TestCase::isCombinator, 1);
    expectSymbolCount(parse.getSyntaxTree(), ElementSymbol.TypeSelector, 2);
  }

  @ParameterizedTest
  @ValueSource(strings = {"*.b", ".b"})
  void ensureSimpleSelectorSequenceWorks(String value) {
    val parse = parser.parse(value);
    expectSymbolCount(parse.getSyntaxTree(), ElementSymbol.ClassSelector, 1);
  }

  @Test
  void ensureComplexSelectorStatementWorks() {
    val expr = "*.a.b.c b > c ~ d + e f .g #h";
    val t = parser.parse(expr).getSyntaxTree();
    expectSymbolCount(t, ElementSymbol.ClassSelector, 4);
  }
}
