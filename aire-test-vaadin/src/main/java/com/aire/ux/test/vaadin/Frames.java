package com.aire.ux.test.vaadin;

import static com.aire.ux.test.vaadin.VaadinExtension.ROOT_AIRE_NAMESPACE;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.NoSuchElementException;
import java.util.function.Function;
import lombok.val;
import org.junit.jupiter.api.extension.ExtensionContext;

public class Frames {

  @SuppressWarnings("unchecked")
  public static Deque<TestFrame> resolveFrameStack(ExtensionContext context) {
    val testClass = context.getRequiredTestClass();
    return
        (Deque<TestFrame>)
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

}
