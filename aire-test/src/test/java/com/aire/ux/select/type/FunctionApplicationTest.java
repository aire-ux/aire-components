package com.aire.ux.select.type;

import com.aire.ux.select.css.CssSelectorParser.ElementSymbol;
import com.aire.ux.select.css.CssSelectorParserTest.TestCase;
import lombok.val;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

public class FunctionApplicationTest extends TestCase {

  @ParameterizedTest
  @ValueSource(strings = {"p:nth-last-of-type(2)", ":hello(2)"})
  void ensureSimpleFunctionApplicationWorks(String value) {
    val t = parser.parse(value);
    expectSymbolCount(t.getSyntaxTree(), ElementSymbol.FunctionApplication, 1);
  }

  @ParameterizedTest
  @ValueSource(strings = {"p:nth-last-of-type(2):hello(2em)", ":hello(2):world(4)"})
  void ensureMultipleFunctionApplicationWorks(String value) {
    val t = parser.parse(value);
    System.out.println(t.getSyntaxTree());
    expectSymbolCount(t.getSyntaxTree(), ElementSymbol.FunctionApplication, 2);
  }
}
