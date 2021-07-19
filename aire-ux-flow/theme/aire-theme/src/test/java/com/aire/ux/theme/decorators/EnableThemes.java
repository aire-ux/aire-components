package com.aire.ux.theme.decorators;

import com.aire.ux.Theme;
import io.sunshower.lang.events.EventListener;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.extension.ExtendWith;

@Order(10)
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@ExtendWith({AireThemeExtension.class})
public @interface EnableThemes {

  Class<? extends EventListener<Theme>>[] listeners() default {};
}
