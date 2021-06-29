package com.aire.ux.test;

import com.aire.ux.test.vaadin.VaadinExtension;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.extension.ExtendWith;

@Order(50)
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@ExtendWith(VaadinExtension.class)
public @interface AireTest {}
