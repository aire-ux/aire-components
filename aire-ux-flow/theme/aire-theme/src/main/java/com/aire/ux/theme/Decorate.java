package com.aire.ux.theme;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * specify which theme decorator should be applied to this component
 *
 * <p>For instance, a material-UI based theme will require different theme-classes than a bootstrap
 * theme. The theme-decorator for this will specify which are applied
 */
@Documented
@Inherited
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface Decorate {
  Class<?> value() default Void.class;
}
