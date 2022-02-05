package io.sunshower.zephyr.ui.rmi;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.Collection;
import java.util.List;

@Documented
@Target(ElementType.TYPE)
@Repeatable(Arguments.class)
@Retention(RetentionPolicy.RUNTIME)
public @interface Argument {

  boolean collection() default false;


  Class<?> type();

  Class<? extends Collection> collectionType() default List.class;
}
