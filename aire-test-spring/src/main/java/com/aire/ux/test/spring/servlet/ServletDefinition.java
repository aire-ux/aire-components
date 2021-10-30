package com.aire.ux.test.spring.servlet;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import javax.servlet.Servlet;

@Retention(RetentionPolicy.RUNTIME)
public @interface ServletDefinition {

  String[] paths();

  Class<? extends Servlet> type();
}
