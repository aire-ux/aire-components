package com.aire.ux.test.vaadin;

import com.aire.ux.test.Navigate;
import com.aire.ux.test.Utilities;
import java.lang.reflect.AnnotatedElement;
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
    val type = context.getRequiredTestClass();
    navigate(type, context);
  }

  @Override
  public void beforeEach(ExtensionContext context) throws Exception {
    val method = context.getRequiredTestMethod();
    navigate(method, context);
  }

  @Override
  public void afterAll(ExtensionContext context) throws Exception {
    val type = context.getRequiredTestClass();
    restore(type, context);
  }

  @Override
  public void afterEach(ExtensionContext context) throws Exception {
    val method = context.getRequiredTestMethod();
    restore(method, context);
  }

  private void restore(AnnotatedElement element, ExtensionContext context) {
    if (element.isAnnotationPresent(Navigate.class)) {
      val viewTestAnnotation = element.getAnnotation(Navigate.class);
      if (!Utilities.isDefault(viewTestAnnotation)) {
        val previous = Frames.resolveFrameStack(context).peek();
        if (previous != null) {
          previous.restore();
        }
      }
    }
  }

  private void navigate(AnnotatedElement element, ExtensionContext context) {
    if (element.isAnnotationPresent(Navigate.class)) {
      val viewTestAnnotation = element.getAnnotation(Navigate.class);
      if (!Utilities.isDefault(viewTestAnnotation)) {
        val value = Utilities.firstNonDefault(viewTestAnnotation.value(), viewTestAnnotation.to());
        Frames.resolveCurrentFrame(context).navigateTo(value);
      }
    }
  }
}
