package com.aire.ux.test.vaadin;

import com.aire.ux.test.AireExtension;
import com.aire.ux.test.AireTest;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.ServiceLoader;
import java.util.ServiceLoader.Provider;
import java.util.function.Function;
import java.util.logging.Level;
import java.util.stream.Stream;
import lombok.extern.java.Log;
import lombok.val;
import org.junit.jupiter.api.extension.AfterAllCallback;
import org.junit.jupiter.api.extension.AfterEachCallback;
import org.junit.jupiter.api.extension.BeforeAllCallback;
import org.junit.jupiter.api.extension.BeforeEachCallback;
import org.junit.jupiter.api.extension.Extension;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.ExtensionContext.Namespace;
import org.junit.jupiter.api.extension.TestTemplateInvocationContext;
import org.junit.jupiter.api.extension.TestTemplateInvocationContextProvider;

@Log
public class VaadinExtension
    implements AireExtension,
        Extension,
        BeforeEachCallback,
        AfterEachCallback,
        BeforeAllCallback,
        AfterAllCallback,
        TestTemplateInvocationContextProvider {

  static final Namespace ROOT_AIRE_NAMESPACE = Namespace.create("aire:root");

  /**
   * @param context are we annotated with {@link @AireTest}?
   * @return true if we are, false otherwise
   */
  @Override
  public boolean supportsTestTemplate(ExtensionContext context) {
    val type = context.getRequiredTestClass();
    if (log.isLoggable(Level.FINE)) {
      log.log(Level.FINE, "Checking AireExtension support for {}", type);
    }
    boolean supported = type.isAnnotationPresent(AireTest.class);
    if (log.isLoggable(Level.FINE)) {
      if (supported) {
        log.log(Level.FINE, "AireExtension is supported for type {}", type);
      } else {
        log.log(Level.FINE, "AireExtension is not supported for type {}", type);
      }
    }
    return supported;
  }

  /**
   * @param context the context to check against
   * @return the AireVaadin root invocation context if we're applicable
   */
  @Override
  public Stream<TestTemplateInvocationContext> provideTestTemplateInvocationContexts(
      ExtensionContext context) {
    return Stream.of(new VaadinViewTemplateInvocationContext());
  }

  /**
   * set up an Aire test context surrounding the entire class
   *
   * <p>1. Determine which Routes to include 2. If there's a surrounding test-context, deactivate it
   * (but don't close it) 3. Create a new test context for the executing class and push it onto the
   * stack
   *
   * @param context the context
   * @throws Exception TODO
   */
  @Override
  @SuppressWarnings("unchecked")
  public void beforeAll(ExtensionContext context) throws Exception {
    val testClass = context.getRequiredTestClass();
    val stack =
        (Deque<TestFrame>)
            context
                .getStore(ROOT_AIRE_NAMESPACE)
                .getOrComputeIfAbsent(
                    testClass, (Function<Class<?>, Deque<TestFrame>>) aClass -> new ArrayDeque<>());

    if (!stack.isEmpty()) {
      deactivateFrame(stack.peek());
    }
    stack.push(createFrame(context));
  }


  private void deactivateFrame(TestFrame peek) {}

  /**
   * If there's an active test-context for this class, pop it off and destroy it
   *
   * @param context
   * @throws Exception
   */
  @Override
  public void afterAll(ExtensionContext context) throws Exception {}

  @Override
  public void afterEach(ExtensionContext context) throws Exception {}

  @Override
  public void beforeEach(ExtensionContext context) throws Exception {}

  private RuntimeException noMatchingProvider(ExtensionContext context) {
    return new IllegalArgumentException(
        "No RoutesCreatorFactory for context: %s, extension: %s".formatted(context, this));
  }

  private TestFrame createFrame(ExtensionContext context) {
    val loader =
        ServiceLoader.load(
            RoutesCreatorFactory.class, Thread.currentThread().getContextClassLoader());
    return loader.stream()
        .map(Provider::get)
        .filter(t -> t.appliesTo(context, this))
        .findFirst()
        .map(t -> new TestFrame(t.create(context, this)))
        .orElseThrow(() -> noMatchingProvider(context));
  }
}
