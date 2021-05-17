package com.aire.ux.plan;

import static org.junit.jupiter.api.Assertions.assertTrue;

import com.aire.ux.parsers.ast.Symbol;
import com.aire.ux.parsers.ast.SyntaxNode;
import com.aire.ux.select.css.CssSelectorParserTest.TestCase;
import com.aire.ux.select.css.Token;
import io.sunshower.lambda.Option;
import lombok.val;
import org.junit.jupiter.api.Test;

class ExpressionTest extends TestCase {

  @Test
  void ensureExpressionWorks() {
    val expr = parse("n");
    assertTrue(expr instanceof Expression.Variable);
  }

  Expression parse(String value) {
    val result = parser.parse(":nth-child(%s)".formatted(value));
    val search = Symbol.symbol("nth-child");
    return result
        .getSyntaxTree()
        .reduce(
            Option.<SyntaxNode<Symbol, Token>>none(),
            (n, k) -> {
              if (k.isSome()) {
                return k;
              }
              if (n.getSymbol().equals(search)) {
                return Option.of(n);
              }
              return Option.none();
            })
        .fmap(t -> Expression.parse(t.getChildren()))
        .orElseThrow(
            () -> new IllegalArgumentException("Failed to parse expression '%s'".formatted(value)));
  }
}
