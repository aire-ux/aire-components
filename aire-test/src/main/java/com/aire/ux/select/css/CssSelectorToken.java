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

/** pure-java implementation of https://www.w3.org/TR/2018/REC-selectors-3-20181106/#lex */
public enum CssSelectorToken implements Type {

  /** identifier: an entity name */
  String("%s|%s".formatted(STRING_FORM_1, STRING_FORM_2)),

  /** Numeric value */
  Numeric(NUMBER),

  /** Lex unclosed strings */
  UnclosedString("%s|%s".formatted(UNCLOSED_STRING_FORM_1, UNCLOSED_STRING_FORM_2)),

  StrictEqualityOperator("="),

  AttributeValueInSetOperator("~="),

  DashedPrefixOperator("\\|="),

  PrefixOperator("\\^="),

  SuffixOperator("\\$="),

  SubstringOperator("\\*="),

  FunctionStart("%s\\(".formatted(IDENTIFIER)),

  FunctionEnd("\\)"),

  AttributeGroupStart("\\["),

  AttributeGroupEnd("\\]"),

  IdentifierSelector("\\#"),

  AdditionOperator("\s*\\+"),

  GreaterThan("\s*>"),

  Comma("\s*,"),

  Tilde("\s*~"),

  Universal("\s*\\*"),

  Not(":not\\("),

  AtKeyword("@%s".formatted(IDENTIFIER)),

  Percentage(NUMBER + "%"),

  Dimension("%s%s".formatted(NUMBER, IDENTIFIER)),

  Class("\\."),

  Whitespace("\s+"),

  FirstLine(":first-line"),

  FirstLetter(":first-letter"),

  Before(":before"),

  After(":after"),

  PseudoElement(":%s".formatted(IDENTIFIER)),

  PseudoClass("::%s".formatted(IDENTIFIER)),

  Identifier(IDENTIFIER);

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
