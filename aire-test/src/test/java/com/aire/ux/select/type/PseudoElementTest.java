package com.aire.ux.select.type;

import com.aire.ux.select.css.CssSelectorParser.ElementSymbol;
import com.aire.ux.select.css.CssSelectorParserTest.TestCase;
import lombok.val;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

class PseudoElementTest extends TestCase {

  @ParameterizedTest
  @ValueSource(strings = {"::hello", "::cool-beans", "::-cool-beans"})
  void ensurePseudoClassWorks(String value) {
    val result = parser.parse(value);
    expectSymbolCount(result.getSyntaxTree(), ElementSymbol.PseudoClass, 1);
    expectSymbolCount(result.getSyntaxTree(), ElementSymbol.TypeSelector, 1);
  }

  @ParameterizedTest
  @ValueSource(strings = {":hello", ":cool-beans", ":-cool-beans"})
  void ensurePseudoElementWorks(String value) {
    val result = parser.parse(value);
    expectSymbolCount(result.getSyntaxTree(), ElementSymbol.PseudoElement, 1);
    expectSymbolCount(result.getSyntaxTree(), ElementSymbol.TypeSelector, 1);
  }
}
