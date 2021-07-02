package com.aire.ux;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * set the default theme.  Since themes can
 * be swapped out dynamically, this is intended to
 * be the default before any theme management is applied
 */
@Documented
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface DefaultTheme {

  /**
   * @return the default theme (if it exists)
   */
  Class<? extends Theme> value() default Theme.class;

}
