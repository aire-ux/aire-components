package com.aire.ux.test.vaadin;

import com.aire.ux.test.AireTest;
import java.lang.reflect.Method;
import java.util.Deque;
import java.util.List;
import java.util.logging.Level;
import java.util.stream.Stream;
import lombok.extern.java.Log;
import lombok.val;
import org.junit.jupiter.api.extension.Extension;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.ParameterContext;
import org.junit.jupiter.api.extension.ParameterResolutionException;
import org.junit.jupiter.api.extension.ParameterResolver;
import org.junit.jupiter.api.extension.TestTemplateInvocationContext;
import org.junit.jupiter.api.extension.TestTemplateInvocationContextProvider;

@Log
public class VaadinViewTemplateInvocationContext
    implements TestTemplateInvocationContext, TestTemplateInvocationContextProvider {

  public String getDisplayName(int invocationIndex) {
    return Frames.currentContext().getRequiredTestClass().getName();
  }

  @Override
  public boolean supportsTestTemplate(ExtensionContext context) {
    val type = context.getRequiredTestClass();
    if (log.isLoggable(Level.FINE)) {
      log.log(Level.FINE, "Checking AireExtension support for {}", type);
    }
    boolean supported = type.isAnnotationPresent(AireTest.class);
    logIsSupported(type, supported);
    return supported;
  }

  @Override
  public Stream<TestTemplateInvocationContext> provideTestTemplateInvocationContexts(
      ExtensionContext extensionContext) {
    return Stream.of(new VaadinViewTestTemplateInvocationContext(extensionContext));
  }

  private void logIsSupported(Class<?> type, boolean supported) {
    if (log.isLoggable(Level.FINE)) {
      if (supported) {
        log.log(Level.FINE, "AireExtension is supported for type {}", type);
      } else {
        log.log(Level.FINE, "AireExtension is not supported for type {}", type);
      }
    }
  }

  static class ViewTestParameterResolver implements ParameterResolver {

    @Override
    @SuppressWarnings("unchecked")
    public boolean supportsParameter(
        ParameterContext parameterContext, ExtensionContext extensionContext)
        throws ParameterResolutionException {
      val ext = extensionContext.getRequiredTestClass();
      val parameter = parameterContext.getParameter();
      val deque =
          (Deque<TestFrame>)
              extensionContext.getStore(VaadinExtension.ROOT_AIRE_NAMESPACE).get(ext);
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
      val deque =
          (Deque<TestFrame>)
              extensionContext.getStore(VaadinExtension.ROOT_AIRE_NAMESPACE).get(ext);
      val frame = deque.peek();
      return frame.getElementResolver(parameter).resolve();
    }
  }

  private static final record VaadinViewTestTemplateInvocationContext(
      ExtensionContext extensionContext)
      implements TestTemplateInvocationContext {

    @Override
    public String getDisplayName(int invocationIndex) {
      val method = extensionContext.getTestMethod();
      return method.map(Method::toGenericString).orElse("<none>");
    }

    @Override
    public List<Extension> getAdditionalExtensions() {
      return List.of(new VaadinViewTemplateInvocationContext.ViewTestParameterResolver());
    }
  }
}
