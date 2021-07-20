package com.aire.ux.test.vaadin;

import com.aire.ux.test.AireExtension;
import com.aire.ux.test.ViewTest;
import java.util.Objects;
import java.util.ServiceLoader;
import java.util.ServiceLoader.Provider;
import java.util.stream.Stream;
import lombok.extern.java.Log;
import lombok.val;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.extension.AfterAllCallback;
import org.junit.jupiter.api.extension.AfterEachCallback;
import org.junit.jupiter.api.extension.BeforeAllCallback;
import org.junit.jupiter.api.extension.BeforeEachCallback;
import org.junit.jupiter.api.extension.Extension;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.ExtensionContext.Namespace;

/**
 * lifecycle is:
 *
 * <p>1. TestClass: a. Create Frame b. Activate Frame 2. TestMethodBegin: a. Overrides? Create Frame
 * b. Overrides? Activate Frame 3. TestMethodEnd: a. Overrides? Get Current Frame b. Overrides?
 * Deactivate Current Frame c. Overrides? Pop Current Frame 4. TestClassEnd: a. Deactivate Current
 * Frame b. Pop Current Frame
 */
@Log
@Order(50)
public class VaadinExtension
    implements AireExtension,
        Extension,
        BeforeEachCallback,
        AfterEachCallback,
        BeforeAllCallback,
        AfterAllCallback {

  static final Namespace ROOT_AIRE_NAMESPACE = Namespace.create("aire:root");

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
    activateFrame(context);
  }

  @Override
  public void beforeEach(ExtensionContext context) throws Exception {
    context
        .getTestMethod()
        .ifPresent(
            method -> {
              Frames.enter(method);
              if (method.isAnnotationPresent(ViewTest.class)) {
                activateFrame(context);
              }
            });
  }

  @Override
  public void afterEach(ExtensionContext context) throws Exception {
    context
        .getTestMethod()
        .ifPresent(
            method -> {
              if (method.isAnnotationPresent(ViewTest.class)) {
                deactivateFrame(context);
              }
              val exited = Frames.exit();
              if (!Objects.equals(exited, method)) {
                throw new IllegalStateException(
                    String.format(
                        "Expected current test method to be %s, but was %s", exited, method));
              }
            });
  }

  /**
   * If there's an active test-context for this class, pop it off and destroy it
   *
   * @param context
   * @throws Exception
   */
  @Override
  public void afterAll(ExtensionContext context) throws Exception {
    deactivateFrame(context);
  }

  private void activateFrame(ExtensionContext context) {
    val stack = Frames.resolveFrameStack(context);
    Frames.pushContext(context);
    if (!stack.isEmpty()) {
      stack.peek().deactivate();
    }
    stack.push(createFrame(context));
  }

  private TestFrame createFrame(ExtensionContext context) {
    val frame =
        routesCreatorFactories()
            .filter(t -> t.appliesTo(context, this))
            .findFirst()
            .map(t -> new TestFrame(t.create(context, this), context))
            .orElseGet(
                () ->
                    new TestFrame(
                        new DefaultRoutesCreatorFactory().create(context, this), context));
    frame.activate();
    return frame;
  }

  private void deactivateFrame(ExtensionContext context) {
    val stack = Frames.resolveFrameStack(context);
    if (stack == null) {
      throw new IllegalStateException("No current test lifecycle");
    }
    val currentFrame = stack.pop();
    if (currentFrame == null) {
      throw new IllegalStateException("Unbalanced test lifecycle");
    }
    currentFrame.deactivate();

    if (!stack.isEmpty()) {
      stack.peek().activate();
    }

    Frames.popContext();
  }

  private Stream<RoutesCreatorFactory> routesCreatorFactories() {
    return ServiceLoader.load(
            RoutesCreatorFactory.class, Thread.currentThread().getContextClassLoader())
        .stream()
        .map(Provider::get);
  }
}
