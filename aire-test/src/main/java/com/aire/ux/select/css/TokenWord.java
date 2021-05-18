package com.aire.ux.select.css;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

/**
 * idk why lombok isn't recognizing generated getters
 */
@SuppressFBWarnings
public final record TokenWord(int start, int end, String lexeme, Type type)
    implements Token {

  @Override
  public String getLexeme() {
    return lexeme;
  }

  @Override
  public int getEnd() {
    return end;
  }

  @Override
  public int getStart() {
    return start;
  }

  @Override
  public Token setType(Type type) {
    return new TokenWord(start, end, lexeme, type);
  }

  @Override
  public Type getType() {
    return type;
  }
}
