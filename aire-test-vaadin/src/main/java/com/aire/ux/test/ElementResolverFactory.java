package com.aire.ux.test;

import java.lang.reflect.AnnotatedElement;
import org.junit.jupiter.api.extension.ExtensionContext;

public interface ElementResolverFactory {

  boolean appliesTo(AnnotatedElement element);


  ElementResolver create(AnnotatedElement element);


}
