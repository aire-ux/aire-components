package com.aire.ux.select.css;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.annotation.Nonnull;

public interface Type {

  @Nonnull
  default Matcher matcher(@Nonnull CharSequence sequence) {
    return getPattern().matcher(sequence);
  }

  /** @return the pattern backing this token type */
  @Nonnull
  Pattern getPattern();
}
