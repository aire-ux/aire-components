package io.sunshower.zephyr.ui.components.beanform;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Documented
@Inherited
@FieldMarker
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Password {}
