package com.aire.ux.test;

import com.aire.ux.test.vaadin.VaadinNavigationExtension;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.junit.jupiter.api.extension.ExtendWith;

/** */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@ExtendWith(VaadinNavigationExtension.class)
@Target({ElementType.METHOD, ElementType.TYPE})
public @interface Routes {

  /**
   * specify which package(s) to load routes into a test-frame from
   *
   * @return the routes to create a frame from
   */
  String scanPackage() default Select.default_value;

  Class<?> scanClassPackage() default Void.class;
}
