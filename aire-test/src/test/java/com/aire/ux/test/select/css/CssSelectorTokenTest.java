package com.aire.ux.test.select.css;

import static org.junit.jupiter.api.Assertions.*;

import lombok.val;
import org.junit.jupiter.api.Test;

class CssSelectorTokenTest {

  @Test
  void ensureElementRegexMatchesSimpleElement() {
    val matcher = CssSelectorToken.Element.getPattern().matcher("p");
    assertTrue(matcher.matches());
    val group = matcher.group(CssSelectorToken.Element.name());
    assertEquals(group, "p");
  }

  @Test
  void ensureElementRegexMatchesSnakeCase() {
    val matcher = CssSelectorToken.Element.getPattern().matcher("p-u");
    assertTrue(matcher.matches());
    val group = matcher.group(CssSelectorToken.Element.name());
    assertEquals(group, "p-u");
  }

  @Test
  void ensureElementRegexMatchesLongElementName() {
    val matcher = CssSelectorToken.Element.getPattern().matcher("hello-world-coolbeans1");
    assertTrue(matcher.matches());
    val group = matcher.group(CssSelectorToken.Element.name());
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
    val matcher = CssSelectorToken.Class.getPattern().matcher(".p");
    assertTrue(matcher.matches());
    val group = matcher.group(CssSelectorToken.Class.name());
    assertEquals(group, ".p");
  }

  @Test
  void ensureIdMatcherIsSelected() {
    val matcher = CssSelectorToken.Identifier.getPattern().matcher("#my-porgler");
    assertTrue(matcher.matches());
    val group = matcher.group(CssSelectorToken.Identifier.name());
    assertEquals(group, "#my-porgler");
  }

  @Test
  void ensureSiblingTokenWorks() {
    val matcher = CssSelectorToken.NextSibling.getPattern().matcher("+");
    assertTrue(matcher.matches());
    val group = matcher.group(CssSelectorToken.NextSibling.name());
    assertEquals(group, "+");
  }

  @Test
  void ensureChildTokenWorks() {
    val matcher = CssSelectorToken.Child.getPattern().matcher(">");
    assertTrue(matcher.matches());
    val group = matcher.group(CssSelectorToken.Child.name());
    assertEquals(group, ">");
  }

  @Test
  void ensureAttributeStartWorks() {
    expect("[", CssSelectorToken.AttributeOpen, "[");
  }

  @Test
  void ensureAttributeEndWorks() {
    expect("]", CssSelectorToken.AttributeClose, "]");
  }

  @Test
  void ensureStringLiteralWorks() {
    expect("\"hello world! How are you?\"", CssSelectorToken.StringLiteral, "\"hello world! How are you?\"");
  }


  private void expect(String expr, CssSelectorToken token, String lexeme) {
    val matcher = token.getPattern().matcher(expr);
    assertTrue(matcher.matches());
    val group = matcher.group(token.name());
    assertEquals(group, lexeme);
  }

}
