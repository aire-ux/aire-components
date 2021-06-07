package com.aire.ux.test;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/** specify which route a @ViewTest begins on */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD, ElementType.TYPE})
public @interface Navigate {

  /** @return the location to navigate to for the test */
  String to() default Select.default_value;

  /** @return the location to navigate to for the test alias for @to */
  String value() default Select.default_value;
}
