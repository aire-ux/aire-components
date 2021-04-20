package com.aire.ux.select.css;

import static com.aire.ux.parsers.LookaheadIterator.wrap;
import static com.aire.ux.select.css.CssSelectorToken.AdditionOperator;
import static com.aire.ux.select.css.CssSelectorToken.ApplicationEnd;
import static com.aire.ux.select.css.CssSelectorToken.AttributeGroupEnd;
import static com.aire.ux.select.css.CssSelectorToken.AttributeGroupStart;
import static com.aire.ux.select.css.CssSelectorToken.AttributeValueInSetOperator;
import static com.aire.ux.select.css.CssSelectorToken.Dimension;
import static com.aire.ux.select.css.CssSelectorToken.FunctionStart;
import static com.aire.ux.select.css.CssSelectorToken.GreaterThan;
import static com.aire.ux.select.css.CssSelectorToken.Identifier;
import static com.aire.ux.select.css.CssSelectorToken.IdentifierSelector;
import static com.aire.ux.select.css.CssSelectorToken.Minus;
import static com.aire.ux.select.css.CssSelectorToken.Not;
import static com.aire.ux.select.css.CssSelectorToken.Numeric;
import static com.aire.ux.select.css.CssSelectorToken.PrefixOperator;
import static com.aire.ux.select.css.CssSelectorToken.PseudoClass;
import static com.aire.ux.select.css.CssSelectorToken.PseudoElement;
import static com.aire.ux.select.css.CssSelectorToken.StrictEqualityOperator;
import static com.aire.ux.select.css.CssSelectorToken.String;
import static com.aire.ux.select.css.CssSelectorToken.SubstringOperator;
import static com.aire.ux.select.css.CssSelectorToken.SuffixOperator;
import static com.aire.ux.select.css.CssSelectorToken.Tilde;
import static com.aire.ux.select.css.CssSelectorToken.Universal;
import static com.aire.ux.select.css.CssSelectorToken.Whitespace;

import com.aire.ux.parsers.LookaheadIterator;
import com.aire.ux.parsers.ast.AbstractSyntaxNode;
import com.aire.ux.parsers.ast.NamedSyntaxNode;
import com.aire.ux.parsers.ast.Symbol;
import com.aire.ux.parsers.ast.SyntaxNode;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.EnumMap;
import java.util.LinkedList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;
import javax.annotation.concurrent.ThreadSafe;
import lombok.val;

@ThreadSafe
@SuppressFBWarnings
public class CssSelectorParser {

  static final Symbol GROUP = new Symbol() {};
  private static final EnumMap<CssSelectorToken, ElementSymbol> mappedTokens;

  static {
    mappedTokens = new EnumMap<CssSelectorToken, ElementSymbol>(CssSelectorToken.class);
    for (val t : ElementSymbol.values()) {
      if (t.token != null) {
        mappedTokens.put(t.token, t);
      }
    }
  }

  private final SelectorLexer lexer;

  public CssSelectorParser() {
    this(new DefaultCssSelectorLexer());
  }

  public CssSelectorParser(final SelectorLexer lexer) {
    this.lexer = lexer;
  }

  static ElementSymbol symbolForToken(CssSelectorToken token) {
    val result = mappedTokens.get(token);
    if (result == null) {
      throw new NoSuchElementException("Error: no associated symbol for token: " + token);
    }
    return result;
  }

  public Selector parse(CharSequence sequence) {
    val tokens = wrap(lexer.lex(sequence).iterator());
    val result = new DefaultSelector();
    val ast = result.getSyntaxTree().getRoot();
    val child = selectorGroup(tokens);
    ast.addChildren(child);
    return result;
  }

  private List<SyntaxNode<Symbol, Token>> selectorGroup(LookaheadIterator<Token> tokens) {
    val result = new LinkedList<SyntaxNode<Symbol, Token>>();
    var selector = new CssSyntaxNode(ElementSymbol.SelectorGroup);
    selector.addChildren(selector(tokens));
    result.add(selector);
    while (tokens.hasNext() && tokens.peek().getType() == CssSelectorToken.Comma) {
      val union = union(tokens);
      result.add(union);
      eatWhitespace(tokens);
      selector = new CssSyntaxNode(ElementSymbol.SelectorGroup);
      selector.addChildren(selector(tokens));
      result.add(selector);
    }
    return result;
  }

  private List<SyntaxNode<Symbol, Token>> selector(LookaheadIterator<Token> tokens) {
    val result = new LinkedList<SyntaxNode<Symbol, Token>>();
    result.addAll(simpleSelectorSequence(tokens));
    while (tokens.hasNext()) {
      val next = tokens.peek();
      if (next.getType() == CssSelectorToken.Comma) {
        break;
      } else if (isSequence(tokens)) {
        while (isSequence(tokens)) {
          result.addAll(simpleSelectorSequence(tokens));
        }
      } else if (isCombinator(tokens)) {
        var current = result.peek();
        while (isCombinator(tokens)) {
          val combinator = combinator(tokens);
          current.addChild(combinator);
          combinator.addChildren(simpleSelectorSequence(tokens));
          current = combinator;
        }
      }
    }
    return result;
  }

  private List<SyntaxNode<Symbol, Token>> simpleSelectorSequence(LookaheadIterator<Token> tokens) {
    val result = new ArrayList<SyntaxNode<Symbol, Token>>();
    if (nextIs(tokens, Identifier, Universal)) {
      val next = tokens.next();
      val nextType = (CssSelectorToken) next.getType();
      result.add(new CssSyntaxNode(symbolForToken(nextType), next));
      while (nextIs(
          tokens,
          IdentifierSelector,
          CssSelectorToken.Class,
          AttributeGroupStart,
          PseudoClass,
          PseudoElement,
          FunctionStart,
          Not)) {
        composite(tokens, result);
      }
    } else {
      while (nextIs(
          tokens,
          IdentifierSelector,
          CssSelectorToken.Class,
          AttributeGroupStart,
          PseudoClass,
          PseudoElement,
          FunctionStart,
          Not)) {
        composite(tokens, result);
      }
    }

    return result;
  }

  private void expectAndDiscard(LookaheadIterator<Token> tokens, CssSelectorToken token) {
    if (!tokens.hasNext()) {
      throw new IllegalArgumentException(
          "Error: expected %s (%s), got EOF".formatted(token, token.getRegularExpression()));
    }

    val next = tokens.next();
    val type = next.getType();

    if (token != type) {
      throw new IllegalArgumentException(
          "Error: expected %s, got %s at (%d, %d): lexeme: %s"
              .formatted(token, type, next.getStart(), next.getEnd(), next.getLexeme()));
    }
  }

  private void composite(LookaheadIterator<Token> tokens, List<SyntaxNode<Symbol, Token>> result) {
    var t = tokens.peek();
    var type = (CssSelectorToken) t.getType();
    if (type == AttributeGroupStart) {
      parseAttributeGroup(tokens, result);
    } else if (type == PseudoClass || type == PseudoElement) {
      pseudo(tokens, result);
    } else if (type == Not) {
      negation(tokens, result);
    } else {
      val selector = new CssSyntaxNode(symbolForToken(type), tokens.next());
      result.add(selector);
      selector.addChild(expect(tokens, Identifier, CssSelectorToken.Class));
    }
  }

  private void negation(LookaheadIterator<Token> tokens, List<SyntaxNode<Symbol, Token>> result) {
    val negation = expect(tokens, Not);
    eatWhitespace(tokens);
    if (isSequence(tokens)) {
      var current = negation;
      var next = simpleSelectorSequence(tokens);
      current.addChildren(next);
      while (isCombinator(tokens)) {
        val combinator = combinator(tokens);
        current.addChild(combinator);
        combinator.addChildren(simpleSelectorSequence(tokens));
        current = combinator;
      }
    } else if (nextIs(tokens, AttributeGroupStart)) {
      val results = new ArrayList<SyntaxNode<Symbol, Token>>();
      parseAttributeGroup(tokens, results);
      negation.addChildren(results);
    } else if (nextIs(tokens, PseudoElement, PseudoClass)) {
      val results = new ArrayList<SyntaxNode<Symbol, Token>>();
      pseudo(tokens, results);
      negation.addChildren(results);
    }
    eatWhitespace(tokens);
    expectAndDiscard(tokens, ApplicationEnd);
    result.add(negation);
  }

  private void pseudo(LookaheadIterator<Token> tokens, List<SyntaxNode<Symbol, Token>> result) {
    val pseudo = expect(tokens, PseudoClass, PseudoElement);
    result.add(pseudo);
    val next = (CssSelectorToken) tokens.peek().getType();
    if (next == FunctionStart) {
      pseudo.addChild(expression(tokens));
    } else {
      val id = expect(tokens, Identifier);
      pseudo.addChild(id);
    }
  }

  private SyntaxNode<Symbol, Token> expression(LookaheadIterator<Token> tokens) {
    eatWhitespace(tokens);
    val function = expect(tokens, FunctionStart);
    while (nextIs(tokens, AdditionOperator, Minus, Dimension, Numeric, String, Identifier)) {
      function.addChild(
          expect(tokens, AdditionOperator, Minus, Dimension, Numeric, String, Identifier));
      eatWhitespace(tokens);
    }
    expectAndDiscard(tokens, ApplicationEnd);
    return function;
  }

  /**
   * parse a single attribute group
   *
   * @param tokens the current token-set
   * @param result the result to add the element to
   */
  private void parseAttributeGroup(
      LookaheadIterator<Token> tokens, List<SyntaxNode<Symbol, Token>> result) {
    expectAndDiscard(tokens, AttributeGroupStart);
    eatWhitespace(tokens);
    val attribute = expect(tokens, Identifier);
    eatWhitespace(tokens);
    if (!nextIs(
        tokens,
        PrefixOperator,
        SuffixOperator,
        SubstringOperator,
        StrictEqualityOperator,
        AttributeValueInSetOperator)) {
      expectAndDiscard(tokens, AttributeGroupEnd);
      result.add(attribute);
      return;
    }

    val operator =
        expect(
            tokens,
            PrefixOperator,
            SuffixOperator,
            SubstringOperator,
            StrictEqualityOperator,
            AttributeValueInSetOperator);

    eatWhitespace(tokens);

    val operand = expect(tokens, Identifier, CssSelectorToken.String);

    attribute.addChildren(List.of(operator, operand));
    eatWhitespace(tokens);
    expectAndDiscard(tokens, AttributeGroupEnd);
    result.add(attribute);
  }

  /**
   * todo: add attrib, pseudo, negation
   *
   * @param tokens
   * @return
   */
  private boolean isSequence(LookaheadIterator<Token> tokens) {
    return nextIs(
        tokens,
        Identifier,
        Universal,
        IdentifierSelector,
        CssSelectorToken.Class,
        PseudoClass,
        PseudoElement,
        Not);
  }

  private boolean isCombinator(LookaheadIterator<Token> tokens) {
    return nextIs(
        tokens, Tilde, Whitespace, GreaterThan, AdditionOperator, PseudoClass, PseudoElement, Not);
  }

  private boolean nextIs(LookaheadIterator<Token> tokens, CssSelectorToken... match) {
    if (tokens.hasNext()) {
      val next = tokens.peek();
      val type = next.getType();
      for (val token : match) {
        if (type.equals(token)) {
          return true;
        }
      }
    }
    return false;
  }

  private SyntaxNode<Symbol, Token> combinator(LookaheadIterator<Token> tokens) {
    val token = tokens.peek();
    val type = (CssSelectorToken) token.getType();

    switch (type) {
      case Tilde:
      case Whitespace:
      case GreaterThan:
      case AdditionOperator:
        val result = new CssSyntaxNode(symbolForToken(type), tokens.next());
        eatWhitespace(tokens);
        return result;
      default:
        throw new IllegalArgumentException(
            "Unknown token in combinator position: %s at (%d,%d): %s"
                .formatted(token.getType(), token.getStart(), token.getEnd(), token.getLexeme()));
    }
  }

  private SyntaxNode<Symbol, Token> union(LookaheadIterator<Token> tokens) {
    val next = tokens.peek();
    if (next.getType() != CssSelectorToken.Comma) {
      throw new IllegalArgumentException(
          "Error: expected ',', got %s (%s)".formatted(next.getType(), next.getLexeme()));
    }
    val node = new CssSyntaxNode(ElementSymbol.Union, tokens.next());
    return node;
  }

  private void eatWhitespace(LookaheadIterator<Token> tokens) {
    while (tokens.hasNext()) {
      val next = tokens.next();
      if (next.getType() != Whitespace) {
        tokens.pushBack(next);
        break;
      }
    }
  }

  private SyntaxNode<Symbol, Token> expect(
      LookaheadIterator<Token> tokens, CssSelectorToken... types) {
    if (!tokens.hasNext()) {
      val expected = Arrays.stream(types).map(t -> t.name()).collect(Collectors.joining(","));
      throw new IllegalArgumentException("Expected one of [%s], got EOF".formatted(expected));
    }
    val next = tokens.peek();
    val nextType = (CssSelectorToken) next.getType();
    for (val type : types) {
      if (nextType.equals(type)) {
        return new CssSyntaxNode(symbolForToken(nextType), tokens.next());
      }
    }
    val expected = Arrays.stream(types).map(t -> t.name()).collect(Collectors.joining(","));
    throw new IllegalArgumentException(
        "Expected one of [%s], got %s (%s)".formatted(expected, nextType, next));
  }

  public enum ElementSymbol implements Symbol {
    SelectorGroup("<group>", null),

    /** operators */
    StringValue("<string>", CssSelectorToken.String),
    PrefixMatch("^=", CssSelectorToken.PrefixOperator),
    SuffixMatch("$=", CssSelectorToken.SuffixOperator),
    SubstringMatch("*=", CssSelectorToken.SubstringOperator),
    DashMatch("|=", CssSelectorToken.DashedPrefixOperator),
    Includes("~=", CssSelectorToken.AttributeValueInSetOperator),
    StrictEquality("=", CssSelectorToken.StrictEqualityOperator),

    Addition("+", AdditionOperator),
    Subtraction("-", CssSelectorToken.Minus),
    Number("<number>", CssSelectorToken.Numeric),

    FunctionApplication("<function>()", FunctionStart),
    /** pseudoclass */
    PseudoClass("::<class>", CssSelectorToken.PseudoClass),
    PseudoElement(":<element>", CssSelectorToken.PseudoElement),
    /** attribute selector */
    AttributeSelector("<attribute>", CssSelectorToken.AttributeGroupStart),
    /** a comma operator denotes set union a,b = union(select(a), select(b)) */
    Union(",", CssSelectorToken.Comma),

    /** the <code>not</code> operator negates enclosed operations */
    Negation(":not", CssSelectorToken.Not),

    /**
     * Universal selector (*) matches any nodes that are descendants of the current selector context
     * unless they are omitted by subsequent selectors
     */
    UniversalSelector("*", CssSelectorToken.Universal),
    /** select by node-type (h1, span, etc.) */
    TypeSelector("<type>", CssSelectorToken.Identifier),

    /** select by class (.red) */
    ClassSelector(".<identifier>", CssSelectorToken.Class),

    /** select children (parent > child) */
    ChildSelector(">", CssSelectorToken.GreaterThan),

    /** select by identity (#my-distinguished-node) */
    IdentitySelector("#<identifier>", CssSelectorToken.IdentifierSelector),

    /** selects any subsequent matching siblings (a ~ b) */
    GeneralSiblingSelector("~", Tilde),

    /** selects any immediately-subsequent matching siblings (a + b) */
    AdjacentSiblingSelector("+", CssSelectorToken.AdditionOperator),

    /** selects any descendants */
    DescendantSelector("<a desc b>", Whitespace);

    private final String value;
    private final CssSelectorToken token;

    ElementSymbol(String value, CssSelectorToken token) {
      this.value = value;
      this.token = token;
    }
  }

  private static class CssSyntaxNode extends NamedSyntaxNode<Symbol, Token> {

    private CssSyntaxNode(ElementSymbol symbol, Token token) {
      super(symbol.name(), symbol, token, symbol);
    }

    /**
     * useful for when there is no associated token
     *
     * @param symbol the symbol to associated with this node
     */
    private CssSyntaxNode(ElementSymbol symbol) {
      this(symbol, null);
    }

    public String toString() {
      val content = getContent();

      return """
          css[symbol:%s, name: %s]{%s}
          """
          .strip()
          .formatted(getSymbol(), getName(), getSource());
    }
  }

  static class IntermediateNode extends AbstractSyntaxNode<Symbol, Token> {

    IntermediateNode() {
      super(null, null, null, null);
    }
  }
}
