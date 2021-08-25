package com.aire.ux.condensation.json;

import static java.lang.String.format;

import com.aire.ux.parsing.ast.Symbol;
import com.aire.ux.parsing.core.TokenBuffer;
import com.aire.ux.parsing.core.Type;
import java.util.Arrays;
import java.util.regex.Pattern;
import javax.annotation.Nonnull;
import lombok.val;

public enum JsonToken implements Type, Symbol {

  Colon(":"),
  Comma(","),
  Null("null"),
  Boolean("true|false"),
  Integer("\\d+"),
  String("\"[^\"\\\\]*(\\\\.[^\"\\\\]*)*\""),
  Addition("\\+"),
  Subtraction("-"),
  OpenBrace("\\{"),
  CloseBrace("}"),
  ArrayOpen("\\["),
  ArrayClose("\\]"),
  Fraction("\\."),
  Exponent("[eE]{1}"),
  WhiteSpace("[ \n\r\f\b  ]+");

  private final String pattern;
  private final boolean include;

  private volatile Pattern cachedPattern;

  JsonToken(@Nonnull String pattern) {
    this(pattern, true);
  }

  JsonToken(@Nonnull String pattern, boolean include) {
    this.pattern = pattern;
    this.include = include;
  }

  @Nonnull
  public static TokenBuffer createTokenBuffer() {
    val buffer = new StringBuilder();
    for (val token : values()) {
      if (token.include) {
        bufferTokenPattern(buffer, token);
      }
    }
    return new TokenBuffer(JsonToken.OpenBrace, buffer);
  }

  private static void bufferTokenPattern(StringBuilder buffer, JsonToken token) {
    buffer.append(format("|(?<%s>%s)", token.name(), token.pattern));
  }

  @Override
  @SuppressWarnings("unchecked")
  public <T extends Type> Iterable<T> enumerate() {
    return () -> Arrays.stream(JsonToken.values()).map(t -> (T) t).iterator();
  }

  @Nonnull
  @Override
  public Pattern getPattern() {
    var result = cachedPattern;
    if (result == null) {
      synchronized (this) {
        result = cachedPattern;
        if (result == null) {
          result = cachedPattern = Pattern.compile(format("(?<%s>%s)", name(), pattern));
        }
      }
    }
    return result;
  }
}