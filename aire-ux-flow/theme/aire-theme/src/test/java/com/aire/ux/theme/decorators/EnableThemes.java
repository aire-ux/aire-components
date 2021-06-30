package com.aire.ux.theme.decorators;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.extension.ExtendWith;

@Order(10)
@Target(ElementType.TYPE)
@ExtendWith({AireThemeExtension.class})
@Retention(RetentionPolicy.RUNTIME)
public @interface EnableThemes {}
