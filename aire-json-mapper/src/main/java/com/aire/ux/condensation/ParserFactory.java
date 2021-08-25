package com.aire.ux.condensation;

public interface ParserFactory {

  boolean supports(String format);

  Parser newParser();
}
