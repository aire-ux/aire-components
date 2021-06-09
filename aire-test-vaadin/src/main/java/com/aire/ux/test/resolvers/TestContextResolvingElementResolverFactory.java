package com.aire.ux.test.resolvers;

import com.aire.ux.test.Context;
import com.aire.ux.test.DefaultTestContext;
import com.aire.ux.test.ElementResolver;
import com.aire.ux.test.ElementResolverFactory;
import com.aire.ux.test.TestContext;
import io.sunshower.arcus.reflect.Reflect;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Parameter;

public class TestContextResolvingElementResolverFactory implements ElementResolverFactory {

  static final TestContext instance;

  static {
    instance = new DefaultTestContext();
  }

  @Override
  public boolean appliesTo(AnnotatedElement element) {
    if (element.isAnnotationPresent(Context.class) && element instanceof Parameter) {
      return Reflect.isCompatible(TestContext.class, ((Parameter) element).getType());
    }
    return false;
  }

  @Override
  public ElementResolver create(AnnotatedElement element) {
    return new TestContextResolvingElementResolver(element);
  }

  private static class TestContextResolvingElementResolver implements ElementResolver {

    public TestContextResolvingElementResolver(AnnotatedElement element) {}

    @Override
    @SuppressWarnings("unchecked")
    public <T> T resolve() {
      return (T) instance;
    }
  }
}
