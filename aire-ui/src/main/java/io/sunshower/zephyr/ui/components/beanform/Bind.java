package io.sunshower.zephyr.ui.components.beanform;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Documented
@FieldMarker
@Retention(RetentionPolicy.RUNTIME)
public @interface Bind {

  String get();
  String set();
}
