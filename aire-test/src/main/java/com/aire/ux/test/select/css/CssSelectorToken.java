package com.aire.ux.test.select.css;

import java.util.Arrays;
import java.util.EnumSet;
import java.util.regex.Pattern;
import javax.annotation.Nonnull;
import lombok.val;

public enum CssSelectorToken implements Type {

  /**
   * CSS class selector cf: https://www.w3.org/TR/selectors-4/#class-html
   */
  Class("\\.\\w{1}[a-zA-Z0-9-]*", WhitespacePolicy.SkipStart, WhitespacePolicy.SkipEnd),

  /**
   * CSS Universal Selector cf: https://www.w3.org/TR/2018/REC-selectors-3-20181106/#universal-selector
   */
  Universal("\\*", WhitespacePolicy.SkipStart, WhitespacePolicy.SkipEnd),

  /**
   * CSS element selector
   */
  Element("\\w{1}[a-zA-Z0-9-]*", WhitespacePolicy.SkipStart, WhitespacePolicy.SkipEnd),

  /**
   * CSS Identifier selector cf: https://www.w3.org/TR/selectors-4/#id-selectors
   */
  Identifier("\\#\\w{1}[a-zA-Z0-9-]*", WhitespacePolicy.SkipStart, WhitespacePolicy.SkipEnd),

  /**
   * Combinators
   */

  /**
   * select immediate children https://www.w3.org/TR/2018/REC-selectors-3-20181106/#descendant-combinators
   */
  Child(">", WhitespacePolicy.SkipStart, WhitespacePolicy.SkipEnd),

  /**
   * Select next sibling https://www.w3.org/TR/2018/REC-selectors-3-20181106/#descendant-combinators
   */
  NextSibling("\\+", WhitespacePolicy.SkipStart, WhitespacePolicy.SkipEnd),

  /**
   * Select subsequent sibling https://www.w3.org/TR/2018/REC-selectors-3-20181106/#descendant-combinators
   */
  SubsequentSibling("~", WhitespacePolicy.SkipStart, WhitespacePolicy.SkipEnd),

  /**
   * Attributes
   */

  /**
   * denote the beginning of an attribute selector expression
   */
  AttributeOpen("\\[", WhitespacePolicy.SkipStart),

  /**
   * denote the end of an attribute selector expression
   */
  AttributeClose("\\]", WhitespacePolicy.SkipEnd),


  StringLiteral("\"([^\\\"]|\\.)*\""),

  /**
   * attribute matching operators
   */

  /**
   * match value exactly
   */
  ExactEqualityOperator("="),

  /**
   * determine if the attribute is in a list of values
    */
  SetContainsOperator("~="),

  /**
   *
    */
  HyphenatedPrefixOperator("\\|="),

  PrefixOperator("\\^="),

  SuffixOperator("\\$="),

  SubstringOperator("\\*=");






  /**
   * immutable state
   */
  private final String pattern;

  /**
   * determine whether the whitespace surrounding a token works
   */
  private final EnumSet<WhitespacePolicy> policies;

  /**
   * mutable internal state
   */
  private volatile Pattern cachedPattern;

  CssSelectorToken(@Nonnull String pattern, @Nonnull WhitespacePolicy... whitespacePolicies) {
    this.pattern = pattern;
    this.policies = EnumSet.noneOf(WhitespacePolicy.class);
    this.policies.addAll(Arrays.asList(whitespacePolicies));
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
    if (token.hasPolicies(WhitespacePolicy.SkipStart, WhitespacePolicy.SkipEnd)) {
      buffer.append("|\\s*(?<%s>%s)\\s*".formatted(token.name(), token.pattern));
    } else if (token.hasPolicy(WhitespacePolicy.SkipStart)) {
      buffer.append("|\\s*(?<%s>%s)".formatted(token.name(), token.pattern));
    } else if (token.hasPolicy(WhitespacePolicy.SkipEnd)) {
      buffer.append("|(?<%s>%s)\\s*".formatted(token.name(), token.pattern));
    } else {
      buffer.append("|(?<%s>%s)".formatted(token.name(), token.pattern));
    }
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

  @Override
  public boolean hasPolicy(@Nonnull WhitespacePolicy policy) {
    return policies.contains(policy);
  }
}
