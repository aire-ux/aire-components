package com.aire.ux.condensation.json;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import com.aire.ux.parsing.core.Token;
import com.aire.ux.parsing.core.Type;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;
import lombok.val;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

class JsonTokenTest {


  private static void expect(CharSequence value, Type... types) {
    val actualTokens = tokenize(value).iterator();
    for (val tokenType : types) {
      if (!actualTokens.hasNext()) {
        fail(String.format("Expected token '%s' but none remained in the buffer", tokenType));
      } else {
        assertEquals(tokenType, actualTokens.next().getType());
      }
    }
    if (actualTokens.hasNext()) {
      val stream = StreamSupport.stream(
          Spliterators.spliteratorUnknownSize(actualTokens, Spliterator.ORDERED), false)
          .map(t -> t.getType().toString());
      fail(String.format("Expected no more tokens, got '%s'",
          stream.collect(Collectors.joining(","))));
    }
  }


  private static Iterable<Token> tokenize(CharSequence value) {
    return JsonToken.createTokenBuffer().tokenize(value);
  }

  @Test
  void ensureOpenBracketIsRecognized() {
    expect("{", JsonToken.OpenBrace, "{");
  }

  @Test
  void ensureClosingBracketIsRecognized() {
    expect("}", JsonToken.CloseBrace, "}");
  }

  @Test
  void ensureBracketsAreTokenized() {
    expect("{}", JsonToken.OpenBrace, JsonToken.CloseBrace);
  }

  @ParameterizedTest
  @ValueSource(strings = {" ", "\n", "\r", "\f", " \n \r \f   "})
  void ensureWhitespaceIsTokenized(String value) {
    expect(value, JsonToken.WhiteSpace);
  }

  @ParameterizedTest
  @ValueSource(strings = {
      "{}",
      "{ }",
      "{  }",
      "{\n "
      + "\n}"
  })
  void ensureEmptyObjectLiteralIsParsedCorrectly(String value) {
    expect(value, JsonToken.OpenBrace, JsonToken.WhiteSpace, JsonToken.CloseBrace);
  }

  @ParameterizedTest
  @ValueSource(strings = {"{\"\"}", "{\"\\\"\"}", "{\"h{el}lo\"}"})
  void ensureLexerHandlesStringsCorrectly(String value) {
    expect(value,
        JsonToken.OpenBrace,
        JsonToken.String,
        JsonToken.CloseBrace
    );
  }

  @ParameterizedTest
  @ValueSource(strings = "[]")
  void ensureArrayBracketsWork(String value) {
    expect(value, JsonToken.ArrayOpen, JsonToken.ArrayClose);
  }

  @Test
  void ensureCharactersWork() {
    expect("+", JsonToken.Addition);
    expect("-", JsonToken.Subtraction);
    expect(":", JsonToken.Colon);

  }


  private void expect(String expr, JsonToken token, String lexeme) {
    val matcher = token.getPattern().matcher(expr);
    assertTrue(matcher.matches());
    val group = matcher.group(token.name());
    assertEquals(group, lexeme);
  }
}