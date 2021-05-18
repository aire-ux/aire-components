package com.aire.ux.parsers.ast;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.util.HashMap;
import java.util.Map;
import javax.annotation.Nonnull;

/** marker for a parsed symbol. Probably best to not overload Symbols and Tokens */
@SuppressWarnings("PMD")
public interface Symbol {

  /**
   * lookup or create a symbol
   *
   * @param value the underlying value
   * @return the symbol
   */
  public static Symbol symbol(@Nonnull String value) {
    return StringSymbol.internmap.computeIfAbsent(value, (k) -> new StringSymbol(k));
  }

  /** @return the name for this symbol */
  default String name() {
    return getClass().getSimpleName();
  }
}

@SuppressFBWarnings
@SuppressWarnings("PMD")
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
