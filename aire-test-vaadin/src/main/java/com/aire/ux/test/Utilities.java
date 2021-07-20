package com.aire.ux.test;

import static java.lang.String.format;

import com.aire.ux.test.Context.Mode;
import io.sunshower.lambda.Option;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import lombok.val;

public class Utilities {

  private Utilities() {}

  /**
   * @param select the annotation to check
   * @return true if the selector is not null and the selector's value or selector expression are
   *     not the default values
   */
  public static boolean isDefault(Select select) {
    return select != null
        && (select.selector().equals(select.value())
            && select.selector().equals(Select.default_value));
  }

  public static boolean isDefault(Navigate test) {
    return test != null
        && Select.default_value.equals(test.to())
        && Select.default_value.equals(test.value());
  }

  public static String firstNonDefault(String... values) {
    for (val value : values) {
      if (!Select.default_value.equals(value)) {
        return value;
      }
    }
    throw new IllegalArgumentException(
        format("No non-default value out of %s", Arrays.asList(values)));
  }

  public static Collection<?> resolveCollection(Class<?> type) {
    if (List.class.isAssignableFrom(type)) {
      return new ArrayList<>();
    }
    if (Set.class.isAssignableFrom(type)) {
      return new LinkedHashSet<>();
    }
    throw new UnsupportedOperationException(format("Can't resolve a collection of type: %s", type));
  }

  @SuppressWarnings("unchecked")
  public static Class<?> resolveCollectionType(Class<?> type) {
    if (List.class.isAssignableFrom(type)) {
      return ArrayList.class;
    }
    if (Set.class.isAssignableFrom(type)) {
      return LinkedHashSet.class;
    }
    throw new UnsupportedOperationException(format("Can't resolve a collection of type: %s", type));
  }

  public static Option<String> notBlank(String s) {
    return (s == null || s.isBlank() ? Option.none() : Option.some(s));
  }

  public static boolean isMode(Context context, Mode mode) {
    if (context == null) {
      return mode == Mode.None;
    }
    return context.mode() == mode;
  }
}
