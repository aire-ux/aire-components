package com.aire.ux.plan;

import static com.aire.ux.select.css.CssSelectorToken.AdditionOperator;
import static com.aire.ux.select.css.CssSelectorToken.Identifier;
import static com.aire.ux.select.css.CssSelectorToken.Minus;
import static com.aire.ux.select.css.CssSelectorToken.Numeric;
import static java.lang.Integer.parseInt;

import com.aire.ux.parsers.LookaheadIterator;
import com.aire.ux.parsers.ast.Symbol;
import com.aire.ux.parsers.ast.SyntaxNode;
import com.aire.ux.plan.Expression.Constant;
import com.aire.ux.plan.Expression.Negation;
import com.aire.ux.plan.Expression.Variable;
import com.aire.ux.select.css.Token;
import com.aire.ux.select.css.Type;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.util.Arrays;
import java.util.List;
import lombok.val;

@SuppressFBWarnings
public interface Expression {

  public static Expression parse(List<SyntaxNode<Symbol, Token>> tokens) {
    val ts = LookaheadIterator.wrap(tokens.iterator());
    if (is(ts, Minus)) {
      expect(ts, Minus);
      return negate(parse(ts));
    } else {
      return parse(ts);
    }
  }

  private static Expression parse(LookaheadIterator<SyntaxNode<Symbol, Token>> tokens) {
    val lhs = readFunctionalPrelude(tokens);
    if (is(tokens, AdditionOperator, Minus)) {
      return readFunctionalBody(lhs, tokens);
    } else {
      return lhs;
    }
  }

  static Expression readFunctionalBody(
      Expression lhs, LookaheadIterator<SyntaxNode<Symbol, Token>> tokens) {
    return null;
  }

  static Expression readFunctionalPrelude(LookaheadIterator<SyntaxNode<Symbol, Token>> tokens) {
    return null;
  }

  static Expression variable(LookaheadIterator<SyntaxNode<Symbol, Token>> tokens) {
    return new Variable(expect(tokens, Identifier).getSource().getLexeme());
  }

  static Expression number(LookaheadIterator<SyntaxNode<Symbol, Token>> tokens) {
    return new Constant(parseInt(expect(tokens, Numeric).getSource().getLexeme()));
  }

  private static Expression negate(Expression expression) {
    return new Negation(expression);
  }

  //
  //  static final record Negation(final Expression negated) extends Expression {
  //
  //  }
  private static SyntaxNode<Symbol, Token> expect(
      LookaheadIterator<SyntaxNode<Symbol, Token>> toks, Type... types) {
    if (!toks.hasNext()) {
      throw new IllegalArgumentException(
          "Error: expected one of %s, got EOF".formatted(Arrays.toString(types)));
    }

    if (!is(toks, types)) {
      throw new IllegalArgumentException(
          "Expected one of %s, got '%s'"
              .formatted(Arrays.toString(types), toks.peek().getSource().getType()));
    }
    return toks.next();
  }

  private static boolean is(LookaheadIterator<SyntaxNode<Symbol, Token>> tokens, Type... test) {
    if (!tokens.hasNext()) {
      return false;
    }
    val next = tokens.peek().getSource();
    for (val token : test) {
      if (token.equals(next.getType())) {
        return true;
      }
    }
    return false;
  }

  public static interface Visitor {}

  public record Constant(int value) implements Expression {}

  public record Variable(String name) implements Expression {}

  @SuppressFBWarnings
  public record Negation(Expression expression) implements Expression {}
}
