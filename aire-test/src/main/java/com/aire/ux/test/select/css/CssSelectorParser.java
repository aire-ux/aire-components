package com.aire.ux.test.select.css;

import static com.aire.ux.parsers.LookaheadIterator.wrap;

import com.aire.ux.parsers.LookaheadIterator;
import com.aire.ux.parsers.ast.AbstractSyntaxNode;
import com.aire.ux.parsers.ast.NamedSyntaxNode;
import com.aire.ux.parsers.ast.Symbol;
import com.aire.ux.parsers.ast.SyntaxNode;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.util.EnumMap;
import java.util.List;
import java.util.NoSuchElementException;
import lombok.val;

@SuppressFBWarnings
public class CssSelectorParser {

  private static final EnumMap<CssSelectorToken, ElementSymbol> mappedTokens;

  static {
    mappedTokens = new EnumMap<CssSelectorToken, ElementSymbol>(CssSelectorToken.class);
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
    val children = selectorsGroup(tokens);
    ast.addChildren(children);
    return result;
  }

  private List<SyntaxNode<Symbol, Token>> selectorsGroup(LookaheadIterator<Token> tokens) {
    return null;
  }

  private SyntaxNode<Symbol, Token> union(LookaheadIterator<Token> tokens) {
    val next = tokens.peek();
    if (next.getType() != CssSelectorToken.Comma) {
      throw new IllegalArgumentException(
          "Error: expected ',', got %s (%s)".formatted(next.getType(), next.getLexeme()));
    }
    val node = new CssSyntaxNode(ElementSymbol.Union, tokens.next());
    eatWhitespace(tokens);
    return node;
  }

  private void eatWhitespace(LookaheadIterator<Token> tokens) {
    while (tokens.hasNext()) {
      val next = tokens.next();
      if (next.getType() != CssSelectorToken.Whitespace) {
        tokens.pushBack(next);
      }
    }
  }

  public enum ElementSymbol implements Symbol {

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
    GeneralSiblingSelector("~", CssSelectorToken.Tilde),

    /** selects any immediately-subsequent matching siblings (a + b) */
    AdjacentSiblingSelector("+", CssSelectorToken.AdditionOperator),

    /** selects any descendants */
    DescendantSelector("<a desc b>", CssSelectorToken.Whitespace);

    final String value;

    ElementSymbol(String value, CssSelectorToken token) {
      this.value = value;
      mappedTokens.put(token, this);
    }
  }

  private static final class CssSyntaxNode extends NamedSyntaxNode<Symbol, Token> {

    private CssSyntaxNode(ElementSymbol symbol, Token token) {
      super(symbol.name(), symbol, token, symbol);
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
