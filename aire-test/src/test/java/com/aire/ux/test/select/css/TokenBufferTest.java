package com.aire.ux.test.select.css;

import static com.aire.ux.test.select.css.CssSelectorToken.AttributeGroupEnd;
import static com.aire.ux.test.select.css.CssSelectorToken.AttributeGroupStart;
import static com.aire.ux.test.select.css.CssSelectorToken.Comma;
import static com.aire.ux.test.select.css.CssSelectorToken.FunctionEnd;
import static com.aire.ux.test.select.css.CssSelectorToken.GreaterThan;
import static com.aire.ux.test.select.css.CssSelectorToken.Identifier;
import static com.aire.ux.test.select.css.CssSelectorToken.Not;
import static com.aire.ux.test.select.css.CssSelectorToken.StrictEqualityOperator;
import static com.aire.ux.test.select.css.CssSelectorToken.Universal;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;
import java.util.stream.Collectors;
import lombok.val;
import org.junit.jupiter.api.Test;

class TokenBufferTest {

  @Test
  void ensureWhitespaceIsChompedCorrectlyForSelectorGroup() {
    val expr = "hello, \t world";
    expectTokens(expr, Identifier, Comma, Identifier);
  }

  @Test
  void ensureComplexOperatorSetWorks() {
    val expr =
        "hello -world- > input:not([type=\"hidden\"]):not([type=\"radio\"]):not([type=\"checkbox\"]) * porglesbees";
    expectTokens(
        expr,
        Identifier,
        Identifier,
        GreaterThan,
        Identifier,
        Not,
        AttributeGroupStart,
        Identifier,
        StrictEqualityOperator,
        CssSelectorToken.String,
        AttributeGroupEnd,
        FunctionEnd,
        Not,
        AttributeGroupStart,
        Identifier,
        StrictEqualityOperator,
        CssSelectorToken.String,
        AttributeGroupEnd,
        FunctionEnd,
        Not,
        AttributeGroupStart,
        Identifier,
        StrictEqualityOperator,
        CssSelectorToken.String,
        AttributeGroupEnd,
        FunctionEnd,
        Universal,
        Identifier);
  }

  @Test
  void ensureStringParsingWorks() {
    expectTokens(
        "\"hello world\"  \"how are you?\"", CssSelectorToken.String, CssSelectorToken.String);
  }

  private void expectTokens(String s, CssSelectorToken... tokens) {
    CssSelectorToken.createTokenBuffer().stream(s).forEach(System.out::println);
    val results =
        CssSelectorToken.createTokenBuffer().stream(s)
            .map(Token::getType)
            .collect(Collectors.toList());
    assertEquals(List.of(tokens), results);
  }
}
