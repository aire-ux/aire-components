package com.aire.ux.test.vaadin;

import java.util.Deque;
import java.util.List;
import lombok.val;
import org.junit.jupiter.api.extension.Extension;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.ParameterContext;
import org.junit.jupiter.api.extension.ParameterResolutionException;
import org.junit.jupiter.api.extension.ParameterResolver;
import org.junit.jupiter.api.extension.TestTemplateInvocationContext;

public class VaadinViewTemplateInvocationContext
    implements TestTemplateInvocationContext, ParameterResolver {

  @Override
  @SuppressWarnings("unchecked")
  public boolean supportsParameter(
      ParameterContext parameterContext, ExtensionContext extensionContext)
      throws ParameterResolutionException {
    val ext = extensionContext.getRequiredTestClass();
    val parameter = parameterContext.getParameter();
    val deque = (Deque<TestFrame>) extensionContext.getStore(VaadinExtension.ROOT_AIRE_NAMESPACE)
        .get(ext);
    val frame = deque.peek();
    return frame != null && frame.hasElementResolver(parameter);
  }

  @Override
  @SuppressWarnings("unchecked")
  public Object resolveParameter(
      ParameterContext parameterContext, ExtensionContext extensionContext)
      throws ParameterResolutionException {

    val ext = extensionContext.getRequiredTestClass();
    val parameter = parameterContext.getParameter();
    val deque = (Deque<TestFrame>) extensionContext.getStore(VaadinExtension.ROOT_AIRE_NAMESPACE)
        .get(ext);
    val frame = deque.peek();
    return frame.getElementResolver(parameter).resolve();
  }


  @Override
  public List<Extension> getAdditionalExtensions() {
    return List.of(new VaadinNavigationExtension());
  }
}
