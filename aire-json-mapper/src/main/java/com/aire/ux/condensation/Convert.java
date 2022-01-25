package com.aire.ux.condensation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.function.Function;

@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD, ElementType.METHOD})
public @interface Convert {

  /** @return a mapper for the key-type */
  Class<? extends Converter> key() default Converter.class;

  Class<? extends Converter> value() default Converter.class;
}
