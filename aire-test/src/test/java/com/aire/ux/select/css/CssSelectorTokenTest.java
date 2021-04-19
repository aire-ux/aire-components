package com.aire.ux.select.css;

import static com.aire.ux.select.css.CssSelectorToken.Identifier;
import static com.aire.ux.select.css.CssSelectorToken.Numeric;
import static com.aire.ux.select.css.CssSelectorToken.UnclosedString;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import lombok.val;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

class CssSelectorTokenTest {


  @Test
  void ensureElementRegexMatchesSimpleElement() {
    val matcher = Identifier.getPattern().matcher("p");
    assertTrue(matcher.matches());
    val group = matcher.group(Identifier.name());
    assertEquals(group, "p");
  }

  @Test
  void ensureElementRegexMatchesSnakeCase() {
    val matcher = Identifier.getPattern().matcher("p-u");
    assertTrue(matcher.matches());
    val group = matcher.group(Identifier.name());
    assertEquals(group, "p-u");
  }

  @Test
  void ensureElementRegexMatchesLongElementName() {
    val matcher = Identifier.getPattern().matcher("hello-world-coolbeans1");
    assertTrue(matcher.matches());
    val group = matcher.group(Identifier.name());
    assertEquals(group, "hello-world-coolbeans1");
  }

  @Test
  void ensureUniversalSelectorIsMatched() {

    val matcher = CssSelectorToken.Universal.getPattern().matcher("*");
    assertTrue(matcher.matches());
    val group = matcher.group(CssSelectorToken.Universal.name());
    assertEquals(group, "*");
  }

  @Test
  void ensureClassSelectorIsMatched() {
    val matcher = CssSelectorToken.Class.getPattern().matcher(".");
    assertTrue(matcher.matches());
    val group = matcher.group(CssSelectorToken.Class.name());
    assertEquals(group, ".");
  }

  @Test
  void ensureIdMatcherIsSelected() {
    val matcher = CssSelectorToken.IdentifierSelector.getPattern().matcher("#");
    assertTrue(matcher.matches());
    val group = matcher.group(CssSelectorToken.IdentifierSelector.name());
    assertEquals(group, "#");
  }

  @Test
  void ensureIdentifierMatchesIdentifiers() {
    expect("hello", Identifier, "hello");
    expect("-hello", Identifier, "-hello");
    expect("-hello-world", Identifier, "-hello-world");
  }

  @Test
  void ensureUnclosedStringsAreMatched() {
    expect("\"hello", UnclosedString, "\"hello");
    expect("\'hello", UnclosedString, "\'hello");
  }

  @Test
  void ensureNumericPatternMatchesNumbers() {
    expect("0", Numeric, "0");
    expect("0.0", Numeric, "0.0");
  }

  @ParameterizedTest
  @ValueSource(strings = {"'string one'",  "\"string ' two\""})
  void ensureStringValuesAreParsedCorrectly(String str) {
    expect(str, CssSelectorToken.String, str);
  }

  private void expect(String expr, CssSelectorToken token, String lexeme) {
    val matcher = token.getPattern().matcher(expr);
    assertTrue(matcher.matches());
    val group = matcher.group(token.name());
    assertEquals(group, lexeme);
  }
}
