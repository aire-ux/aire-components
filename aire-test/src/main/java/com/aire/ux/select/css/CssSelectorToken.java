package com.aire.ux.select.css;

import static com.aire.ux.select.css.TokenPatterns.IDENTIFIER;
import static com.aire.ux.select.css.TokenPatterns.NUMBER;
import static com.aire.ux.select.css.TokenPatterns.STRING_FORM_1;
import static com.aire.ux.select.css.TokenPatterns.STRING_FORM_2;
import static com.aire.ux.select.css.TokenPatterns.UNCLOSED_STRING_FORM_1;
import static com.aire.ux.select.css.TokenPatterns.UNCLOSED_STRING_FORM_2;

import java.util.regex.Pattern;
import javax.annotation.Nonnull;
import lombok.val;

/**
 * pure-java implementation of https://www.w3.org/TR/2018/REC-selectors-3-20181106/#lex re-order
 * these elements with care as their ordinal defines the lexer precedence for tokens
 */
public enum CssSelectorToken implements Type {

  /** identifier: an entity name */
  String("%s|%s".formatted(STRING_FORM_1, STRING_FORM_2)),

  /** Numeric value */
  Numeric(NUMBER),

  /** Lex unclosed strings */
  UnclosedString("%s|%s".formatted(UNCLOSED_STRING_FORM_1, UNCLOSED_STRING_FORM_2)),

  /** strict equality operator <code>=</code> */
  StrictEqualityOperator("="),

  /** attribute value in set */
  AttributeValueInSetOperator("~="),

  /** dashed prefix operator */
  DashedPrefixOperator("\\|="),

  /** */
  PrefixOperator("\\^="),

  /** */
  SuffixOperator("\\$="),

  /** */
  SubstringOperator("\\*="),

  /** */
  FunctionStart("%s\\(".formatted(IDENTIFIER)),

  /** */
  ApplicationEnd("\\)"),

  /** */
  AttributeGroup("\\["),

  /** */
  AttributeGroupEnd("\\]"),

  /** */
  IdentifierSelector("\\#"),

  /** */
  AdditionOperator("\s*\\+"),

  /** greater-than */
  GreaterThan("\s*>"),

  Comma("\s*,"),

  /** tilde */
  Tilde("\s*~(?!=)"),

  /** universal operator */
  Universal("\\*"),

  /** negation prefix */
  Not(":not\\("),

  /** at keyword such as @import */
  AtKeyword("@%s".formatted(IDENTIFIER)),

  /** percentage operator */
  Percentage(NUMBER + "%"),

  /** dimension, such as 2em, 1rem, 0.1em */
  Dimension("%s%s".formatted(NUMBER, IDENTIFIER)),

  /** class operator */
  Class("\\."),

  /** whitespace */
  Whitespace("\s+"),

  /** pseudo-class prefix */
  PseudoClass("::"),

  /** pseudo--lower precedent than pseudoclass */
  PseudoElement(":"),

  /** identifier element */
  Identifier(IDENTIFIER),

  /** minus--lower precedent than Identifier */
  Minus("-");

  /** immutable state */
  private final String pattern;

  private final boolean include;

  /** mutable internal state */
  private volatile Pattern cachedPattern;

  CssSelectorToken(@Nonnull String pattern) {
    this(pattern, true);
  }

  CssSelectorToken(@Nonnull String pattern, boolean include) {
    this.pattern = pattern;
    this.include = include;
  }

  public String getRegularExpression() {
    return pattern;
  }

  @Nonnull
  public static TokenBuffer createTokenBuffer() {
    val buffer = new StringBuilder();
    for (val token : values()) {
      if (token.include) {
        bufferTokenPattern(buffer, token);
      }
    }
    return new TokenBuffer(buffer);
  }

  private static void bufferTokenPattern(StringBuilder buffer, CssSelectorToken token) {
    buffer.append("|(?<%s>%s)".formatted(token.name(), token.pattern));
  }

  @Nonnull
  @Override
  public Pattern getPattern() {
    var result = cachedPattern;
    if (result == null) {
      synchronized (this) {
        result = cachedPattern;
        if (result == null) {
          result = cachedPattern = Pattern.compile("(?<%s>%s)".formatted(name(), pattern));
        }
      }
    }
    return result;
  }
}
