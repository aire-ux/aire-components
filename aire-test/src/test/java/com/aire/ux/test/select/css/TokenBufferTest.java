package com.aire.ux.test.select.css;

import static com.aire.ux.test.select.css.CssSelectorToken.Element;
import static com.aire.ux.test.select.css.CssSelectorToken.Universal;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import java.util.stream.Collectors;
import lombok.val;
import org.junit.jupiter.api.Test;

class TokenBufferTest {

  @Test
  void ensureTokenBufferReturnsCorrectTokenForSingleCharacterElement() {
    val iter = CssSelectorToken.createTokenBuffer().tokenize("p").iterator();
    assertTrue(iter.hasNext());
    val next = iter.next();
    assertEquals(next.getStart(), 0);
    assertEquals(next.getEnd(), 1);
    assertEquals(next.getType(), Element);
    assertEquals(next.getLexeme(), "p");
  }

  @Test
  void ensureTokenBufferReturnsCorrectTokenForComplexCharacterElement() {
    val elementName = "vaadin-button-button123";
    val iter = CssSelectorToken.createTokenBuffer().tokenize(elementName).iterator();
    assertTrue(iter.hasNext());
    val next = iter.next();
    assertEquals(next.getStart(), 0);
    assertEquals(next.getEnd(), elementName.length());
    assertEquals(next.getType(), Element);
    assertEquals(next.getLexeme(), elementName);
  }

  @Test
  void ensureUniversalSelectorIsMatched() {
    val elementName = "*";
    val iter = CssSelectorToken.createTokenBuffer().tokenize(elementName).iterator();
    assertTrue(iter.hasNext());
    val next = iter.next();
    assertEquals(next.getStart(), 0);
    assertEquals(next.getEnd(), elementName.length());
    assertEquals(next.getType(), Universal);
    assertEquals(next.getLexeme(), elementName);
  }

  @Test
  void ensurePreceedingWhitespaceOfElementIsMatched() {
    val expr = " hello";
    val iter = CssSelectorToken.createTokenBuffer().tokenize(expr).iterator();
    assertTrue(iter.hasNext());
    val next = iter.next();
    assertEquals(next.getType(), Element);
    assertEquals(next.getLexeme(), "hello");
  }

  @Test
  void ensureSucceedingWhitespaceOfElementIsMatched() {
    val expr = "hello ";
    val iter = CssSelectorToken.createTokenBuffer().tokenize(expr).iterator();
    assertTrue(iter.hasNext());
    val next = iter.next();
    assertEquals(next.getType(), Element);
    assertEquals(next.getLexeme(), "hello");
  }

  @Test
  void ensureSurroundingWhitespaceOfElementsIsMatched() {
    val expr = " hello ";
    val iter = CssSelectorToken.createTokenBuffer().tokenize(expr).iterator();
    assertTrue(iter.hasNext());
    val next = iter.next();
    assertEquals(next.getType(), Element);
    assertEquals(next.getLexeme(), "hello");
  }

  @Test
  void ensureSelectorsAreParsedCorrectly() {
    val expr = " parent child-element ";
    val iter = CssSelectorToken.createTokenBuffer().tokenize(expr).iterator();
    assertTrue(iter.hasNext());

    var next = iter.next();
    assertEquals("parent", next.getLexeme());
    assertEquals(next.getType(), Element);
    next = iter.next();
    assertEquals("child-element", next.getLexeme());
    assertEquals(next.getType(), Element);
  }

  @Test
  void ensureAlternationBetweenUniversalAndElementWorks() {
    val expr = " hello * world * how are * you";
    val results =
        CssSelectorToken.createTokenBuffer().stream(expr)
            .map(Token::getType)
            .collect(Collectors.toList());
    val expected =
        List.of(Element, Universal, Element, Universal, Element, Element, Universal, Element);
    assertEquals(expected, results);
  }
}
