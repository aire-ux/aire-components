package com.aire.ux.test;

public class Utilities {

  private Utilities() {
  }


  /**
   *
   * @param select the annotation to check
   * @return true if the selector is not null and the selector's value or selector expression
   * are not the default values
   */
  public static boolean isDefault(Select select) {
    return select != null && (select.selector()
        .equals(select.value()) && select.selector().equals(Select.default_value));
  }

  public static boolean isDefault(ViewTest test) {
    return test != null && Select.default_value.equals(test.navigateTo());
  }
}
