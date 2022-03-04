package com.aire.ux.test.vaadin;

import static com.aire.ux.test.vaadin.VaadinExtension.ROOT_AIRE_NAMESPACE;

import com.aire.ux.test.Adapter;
import com.aire.ux.test.ComponentHierarchyNodeAdapter;
import com.aire.ux.test.NodeAdapter;
import com.vaadin.flow.dom.Element;
import io.sunshower.arcus.reflect.Reflect;
import java.lang.reflect.Method;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;
import lombok.val;
import org.junit.jupiter.api.extension.ExtensionContext;

public class Frames {

  static final NodeAdapter<Element> defaultAdapter = new ComponentHierarchyNodeAdapter();
  private static final Deque<Method> executingMethods;
  private static final Deque<ExtensionContext> contexts;

  static {
    contexts = new ArrayDeque<>();
    executingMethods = new ArrayDeque<>();
  }

  public static void enter(Method method) {
    executingMethods.push(method);
  }

  public static Method exit() {
    return executingMethods.pop();
  }

  public static Optional<Method> getCurrentTestMethod() {
    return Optional.ofNullable(executingMethods.peek());
  }

  @SuppressWarnings("unchecked")
  public static NodeAdapter<Element> getCurrentNodeAdapter() {
    val currentTestMethod = getCurrentTestMethod();
    if (currentTestMethod.isPresent()) {
      val method = currentTestMethod.get();
      if (method.isAnnotationPresent(Adapter.class)) {
        return (NodeAdapter<Element>)
            Reflect.instantiate(method.getAnnotation(Adapter.class).value());
      }
    }
    val testClass = currentContext().getRequiredTestClass();
    for (var c = testClass; !Objects.equals(c, Object.class); c = c.getSuperclass()) {
      if (c.isAnnotationPresent(Adapter.class)) {
        return (NodeAdapter<Element>) Reflect.instantiate(c.getAnnotation(Adapter.class).value());
      }
      for (val annotation : c.getDeclaredAnnotations()) {
        val annotationType = annotation.annotationType();
        if (annotationType.isAnnotationPresent(Adapter.class)) {
          return (NodeAdapter<Element>)
              Reflect.instantiate(annotationType.getAnnotation(Adapter.class).value());
        }
      }
    }
    return defaultAdapter;
  }

  @SuppressWarnings("unchecked")
  public static Deque<TestFrame> resolveFrameStack(ExtensionContext context) {
    val testClass = context.getRequiredTestClass();
    return (Deque<TestFrame>)
        context
            .getStore(ROOT_AIRE_NAMESPACE)
            .getOrComputeIfAbsent(
                testClass, (Function<Class<?>, Deque<TestFrame>>) aClass -> new ArrayDeque<>());
  }

  public static TestFrame resolveCurrentFrame(ExtensionContext context) {
    val frame = resolveFrameStack(context).peek();
    if (frame == null) {
      throw new NoSuchElementException("No current frame");
    }
    return frame;
  }

  public static ExtensionContext currentContext() {
    val result = contexts.peek();
    if (result == null) {
      throw new IllegalStateException("Invalid test state: no test context");
    }
    return result;
  }

  public static void popContext() {
    contexts.pop();
  }

  public static void pushContext(ExtensionContext context) {
    contexts.push(context);
  }

  public static TestFrame resolveCurrentFrame() {
    return resolveCurrentFrame(currentContext());
  }

  public static void reload() {
    val currentFrame = resolveCurrentFrame();
    currentFrame.deactivate();
    currentFrame.activate();
  }
}
