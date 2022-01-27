package io.sunshower.zephyr.ui.controls;

import com.vaadin.flow.router.RouterLayout;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface Breadcrumb {

  String name();

  String icon() default "";

  Class<? extends RouterLayout> host() default RouterLayout.class;
}
