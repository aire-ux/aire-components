package com.aire.ux.test;

import com.vaadin.flow.dom.Element;
import io.sunshower.arcus.selectors.test.NodeAdapter;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface Adapter {

  Class<? extends NodeAdapter<? extends Element>> value();
}
