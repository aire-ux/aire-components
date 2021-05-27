package com.aire.ux.test;

import com.aire.ux.test.vaadin.VaadinViewTemplateInvocationContext;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.junit.jupiter.api.TestTemplate;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.platform.commons.annotation.Testable;

@Testable
@Documented
@TestTemplate
@Retention(RetentionPolicy.RUNTIME)
@ExtendWith(VaadinViewTemplateInvocationContext.class)
@Target({ElementType.ANNOTATION_TYPE, ElementType.METHOD})
public @interface ViewTest {}
