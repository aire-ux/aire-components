package com.aire.ux.test.vaadin;

import com.aire.ux.test.Navigate;
import com.aire.ux.test.Utilities;
import java.util.Optional;
import lombok.val;
import org.junit.jupiter.api.extension.AfterAllCallback;
import org.junit.jupiter.api.extension.AfterEachCallback;
import org.junit.jupiter.api.extension.BeforeAllCallback;
import org.junit.jupiter.api.extension.BeforeEachCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.TestTemplateInvocationContext;

public class VaadinNavigationExtension
    implements AfterAllCallback,
    AfterEachCallback,
    BeforeAllCallback,
    BeforeEachCallback,
    TestTemplateInvocationContext {


  @Override
  public void beforeAll(ExtensionContext context) throws Exception {
    resolveNavigationTarget(context).ifPresent(annotation -> navigate(annotation, context));
  }

  @Override
  public void beforeEach(ExtensionContext context) throws Exception {
    resolveNavigationTarget(context).ifPresent(annotation -> navigate(annotation, context));
  }

  @Override
  public void afterAll(ExtensionContext context) throws Exception {
    resolveNavigationTarget(context).ifPresent(annotation -> restore(annotation, context));
  }

  @Override
  public void afterEach(ExtensionContext context) throws Exception {
    resolveNavigationTarget(context).ifPresent(annotation -> restore(annotation, context));
  }

  private void restore(Navigate viewTestAnnotation, ExtensionContext context) {
    if (!Utilities.isDefault(viewTestAnnotation)) {
      val previous = Frames.resolveFrameStack(context).peek();
      if (previous != null) {
        previous.restore();
      }
    }
  }

  private void navigate(Navigate viewTestAnnotation, ExtensionContext context) {
    if (!Utilities.isDefault(viewTestAnnotation)) {
      val value = Utilities.firstNonDefault(viewTestAnnotation.value(), viewTestAnnotation.to());
      Frames.resolveCurrentFrame(context).navigateTo(value);
    }
  }

  private Optional<Navigate> resolveNavigationTarget(ExtensionContext context) {
    return context.getTestMethod()
        .flatMap(method -> Optional.ofNullable(method.getAnnotation(Navigate.class)))
        .or(() -> context.getTestClass()
            .flatMap(t -> Optional.ofNullable(t.getAnnotation(Navigate.class))));
  }
}
