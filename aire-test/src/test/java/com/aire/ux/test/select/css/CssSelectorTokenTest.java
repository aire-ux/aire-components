package com.aire.ux.test.select.css;

import static com.aire.ux.test.select.css.CssSelectorToken.Identifier;
import static com.aire.ux.test.select.css.CssSelectorToken.Numeric;
import static com.aire.ux.test.select.css.CssSelectorToken.UnclosedString;
import static org.junit.jupiter.api.Assertions.*;

import lombok.val;
import org.junit.jupiter.api.Test;

class CssSelectorTokenTest {




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


  private void expect(String expr, CssSelectorToken token, String lexeme) {
    val matcher = token.getPattern().matcher(expr);
    assertTrue(matcher.matches());
    val group = matcher.group(token.name());
    assertEquals(group, lexeme);
  }

}
