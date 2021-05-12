package com.aire.ux.parsers.ast;

import java.util.HashMap;
import java.util.Map;

/**
 * marker for a parsed symbol. Probably best to not overload Symbols and Tokens
 */
public interface Symbol {

  /**
   * lookup or create a symbol
   *
   * @param value the underlying value
   * @return the symbol
   */
  public static Symbol symbol(String value) {
    return StringSymbol.internmap.computeIfAbsent(value, (k) -> new StringSymbol(k));
  }

  /**
   * @return the name for this symbol
   */
  default String name() {
    return getClass().getSimpleName();
  }
}


final record StringSymbol(String symbol) implements Symbol {

  static final Map<String, Symbol> internmap;

  static {
    internmap = new HashMap<>();
  }

  @Override
  public String name() {
    return symbol;
  }


}
