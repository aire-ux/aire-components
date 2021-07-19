package com.aire.ux.test;

import java.lang.reflect.AnnotatedElement;

public interface ElementResolverFactory {


  boolean appliesTo(AnnotatedElement element);

  ElementResolver create(AnnotatedElement element);
}
