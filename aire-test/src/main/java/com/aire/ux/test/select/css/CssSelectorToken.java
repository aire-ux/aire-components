package com.aire.ux.test.select.css;

import static com.aire.ux.test.select.css.TokenPatterns.IDENTIFIER;
import static com.aire.ux.test.select.css.TokenPatterns.NAME_CHARACTER;
import static com.aire.ux.test.select.css.TokenPatterns.NAME_START;
import static com.aire.ux.test.select.css.TokenPatterns.NUMBER;
import static com.aire.ux.test.select.css.TokenPatterns.STRING_FORM_1;
import static com.aire.ux.test.select.css.TokenPatterns.STRING_FORM_2;
import static com.aire.ux.test.select.css.TokenPatterns.UNCLOSED_STRING_FORM_1;
import static com.aire.ux.test.select.css.TokenPatterns.UNCLOSED_STRING_FORM_2;

import java.util.Arrays;
import java.util.EnumSet;
import java.util.regex.Pattern;
import javax.annotation.Nonnull;
import lombok.val;

/**
 * pure-java implementation of https://www.w3.org/TR/2018/REC-selectors-3-20181106/#lex
 */
public enum CssSelectorToken implements Type {

  /**
   * identifier: an entity name
   */

  String("%s|%s".formatted(STRING_FORM_1, STRING_FORM_2)),

  /**
   * Numeric value
   */
  Numeric(NUMBER),

  /**
   * Lex unclosed strings
   */
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

  IdentifierSelector("#%s".formatted(IDENTIFIER)),

  AdditionOperator("\s*\\+"),

  GreaterThan("\s*>"),

  Comma("\s*,"),

  Tilde("\s*~"),

  Universal("\s*\\*"),

  Not(":not\\("),

  AtKeyword("@%s".formatted(IDENTIFIER)),

  Percentage(NUMBER + "%"),

  Dimension("%s%s".formatted(NUMBER, IDENTIFIER)),

  Class("\\.%s".formatted(IDENTIFIER)),

  Identifier(IDENTIFIER);






  /**
   * immutable state
   */
  private final String pattern;


  /**
   * mutable internal state
   */
  private volatile Pattern cachedPattern;

  CssSelectorToken(@Nonnull String pattern) {
    this.pattern = pattern;
  }

  @Nonnull
  public static TokenBuffer createTokenBuffer() {
    val buffer = new StringBuilder();
    for (val token : values()) {
      bufferTokenPattern(buffer, token);
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
