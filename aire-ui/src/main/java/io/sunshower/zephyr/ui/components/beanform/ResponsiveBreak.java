package io.sunshower.zephyr.ui.components.beanform;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Repeatable;
import java.lang.annotation.Target;

@Documented
@Inherited
@Target(ElementType.TYPE)
@Repeatable(ResponsiveBreaks.class)
public @interface ResponsiveBreak {

  String width();

  int columns();

  Position position() default Position.Top;

  enum Position {
    Top,
    Aside
  }
}
