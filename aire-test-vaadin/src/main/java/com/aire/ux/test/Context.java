package com.aire.ux.test;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Documented
@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
public @interface Context {

  /**
   * the mode to create the context value in.
   */
  Mode mode() default Mode.None;

  enum Mode {
    /**
     * spy the context variable
     */
    Spy,

    /**
     * mock the context variable (has no behaviors)
     */
    Mock,

    /**
     * present the context variable with no additional features
     */
    None
  }
}
