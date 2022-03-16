package io.sunshower.zephyr.ui.components;

import com.vaadin.flow.component.icon.IconFactory;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Documented
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface WizardPage {

  String key();

  String title();

  Class<? extends IconFactory> iconFactory() default IconFactory.class;
}
