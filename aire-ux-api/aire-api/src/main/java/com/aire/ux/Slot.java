package com.aire.ux;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * a slot can be a method that inserts a component into a host, or, if this annotation is present on
 * an element, the method is determined as follows:
 *
 * <p>1. An instantiator attempts to locate a public method named {@code setContent}, if it finds
 * it, any component with that target will be added to it. 2. If setContent is not located, then the
 * underlying element will be located and add() or addChild() or addChildren will be called
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD, ElementType.FIELD})
public @interface Slot {

  String value() default Constants.DEFAULT;
}
