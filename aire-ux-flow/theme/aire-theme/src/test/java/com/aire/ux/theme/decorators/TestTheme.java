package com.aire.ux.theme.decorators;

import com.aire.ux.Theme;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Documented
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface TestTheme {

  Class<? extends Theme> value() default Theme.class;
}
