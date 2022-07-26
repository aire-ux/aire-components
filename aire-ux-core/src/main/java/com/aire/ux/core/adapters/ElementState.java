package com.aire.ux.core.adapters;

import io.sunshower.arcus.ast.Symbol;
import io.sunshower.arcus.selectors.test.NodeAdapter;

public enum ElementState implements NodeAdapter.State {
  Hover(":hover"),
  Active(":active"),
  Focused(":focus"),
  FocusVisibile(":focus-visible"),
  FocusWithin(":focus-within");

  private final Symbol symbol;

  ElementState(String symbol) {
    this.symbol = Symbol.symbol(symbol);
  }

  @Override
  public Symbol toSymbol() {
    return symbol;
  }
}
