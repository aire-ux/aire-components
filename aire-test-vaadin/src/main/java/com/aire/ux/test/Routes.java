package com.aire.ux.test;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/** */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD, ElementType.TYPE})
public @interface Routes {

  /**
   * specify which package(s) to load routes into a test-frame from
   *
   * @return the routes to create a frame from
   */
  String scanPackage() default "";
}
