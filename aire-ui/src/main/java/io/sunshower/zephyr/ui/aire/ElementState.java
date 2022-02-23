package io.sunshower.zephyr.ui.aire;

import com.aire.ux.test.NodeAdapter;
import io.sunshower.arcus.ast.Symbol;

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
