package com.aire.ux;

import com.vaadin.flow.component.HasElement;
import java.util.function.Supplier;

public @interface Control {

  String target();

  SelectorMode selectorMode() default SelectorMode.Path;

  Class<? extends Supplier<? extends HasElement>> factory();
}
