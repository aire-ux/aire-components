package com.aire.ux.plan;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.aire.ux.parsers.ast.Symbol;
import com.aire.ux.parsers.ast.SyntaxNode;
import com.aire.ux.select.css.CssSelectorParserTest.TestCase;
import com.aire.ux.select.css.Token;
import io.sunshower.lambda.Option;
import lombok.val;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

class ExpressionTest extends TestCase {

  @Test
  void ensureExpressionWorks() {
    val expr = parse("n");
    assertTrue(expr instanceof Expression.AlgebraicExpression);
  }

  @ParameterizedTest
  @ValueSource(
      strings = {"1n", "n + 3", "n - 2", "3n + 5", "2n + 4", "5n", "n", "0n + 1", "-1n - 4"})
  void ensureAllExpressionCombinationsAreParsedWithoutException(String expr) {
    assertTrue(parse(expr) instanceof Expression);
  }

  @Test
  void ensureSelectionApiMakesSense() {
    val result = parse("2").apply(0);
    assertEquals(2, result);
  }

  @ParameterizedTest
  @ValueSource(ints = {1, 2, 3, 4, 5, 6})
  void ensureMultiplesWork(int v) {
    val expr = parse("4n");
    assertEquals(4 * v, expr.apply(v));
  }

  @ParameterizedTest
  @ValueSource(ints = {1, 2, 3, 4, 5, 6})
  void ensureMultiplesPlusOffsetsWork(int value) {
    val expr = parse("2n + 17");
    assertEquals(2 * value + 17, expr.apply(value));
  }

  @ParameterizedTest
  @ValueSource(ints = {0, 1, 2, 3, 4, 5})
  void ensureNegationWorks(int value) {
    val expr = parse("- n + 14");
    assertEquals(-value + 14, expr.apply(value));
  }

  @ParameterizedTest
  @ValueSource(ints = {0, 1, 2, 3, 4, 5})
  void ensureNegationWorksOverridingTokenPrecedence(int value) {
    val expr = parse("-n + 14");
    assertEquals(-value + 14, expr.apply(value));
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
