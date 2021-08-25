package com.aire.ux.condensation.json;

import com.aire.ux.parsing.core.LookaheadIterator;
import com.aire.ux.parsing.core.Token;
import java.util.Arrays;
import java.util.stream.Collectors;

public class ParsingException extends RuntimeException {

  public ParsingException(LookaheadIterator<Token> tokens, JsonToken... expected) {
  }

  public ParsingException(LookaheadIterator<Token> tokens, Token actual, JsonToken... expected) {
    super(messageFor(tokens, actual, expected));
  }

  static String messageFor(LookaheadIterator<Token> tokens, Token actual, JsonToken... expected) {
    return String
        .format("Expected '%s' at (%d, %d), got '%s'", actual.getLexeme(), actual.getStart(),
            actual.getEnd(),
            Arrays.stream(expected).map(Enum::name).collect(Collectors.joining(",")));
  }
}
