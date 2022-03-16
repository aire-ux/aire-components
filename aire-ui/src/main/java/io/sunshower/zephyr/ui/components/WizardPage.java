package io.sunshower.zephyr.ui.components;

import com.vaadin.flow.component.icon.Icon;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.function.Supplier;

@Documented
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface WizardPage {

  String key();

  String title();

  Class<? extends Supplier<Icon>> iconFactory() default IconSupplier.class;
}
