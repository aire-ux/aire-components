package com.aire.ux.test.select.css;

public class CssSelectorParser {

  private final SelectorLexer lexer;

  public CssSelectorParser() {
    this(new DefaultCssSelectorLexer());
  }

  public CssSelectorParser(final SelectorLexer lexer) {
    this.lexer = lexer;
  }

  public Selector parse(CharSequence sequence) {
    //    val tokens = lexer.lex(sequence).iterator();
    return null;
  }
}
