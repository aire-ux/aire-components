package com.aire.ux.test.select.css;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.annotation.Nonnull;
import lombok.val;

public interface Type {

  enum WhitespacePolicy {
    SkipEnd,
    SkipStart
  }

  @Nonnull
  default Matcher matcher(@Nonnull CharSequence sequence) {
    return getPattern().matcher(sequence);
  }

  /** @return the pattern backing this token type */
  @Nonnull
  Pattern getPattern();

  /**
   * @param policy the policy to check this type for
   * @return true if the policy exists, false otherwise
   */
  boolean hasPolicy(@Nonnull WhitespacePolicy policy);

  default boolean hasPolicies(WhitespacePolicy... policies) {
    for (val policy : policies) {
      if (!hasPolicy(policy)) {
        return false;
      }
    }
    return true;
  }
}
