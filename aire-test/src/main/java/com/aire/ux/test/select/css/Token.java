package com.aire.ux.test.select.css;

public interface Token {

  /** @return the token type */
  Type getType();

  /** @return the lexeme matched */
  String getLexeme();

  /** @return the position indicating the start of the matched lexeme */
  int getEnd();

  /** @return the position indicating the end of the matched lexeme */
  int getStart();

  Token setType(Type type);
}
