package com.aire.ux.test.vaadin;

import com.aire.ux.test.Utilities;
import com.aire.ux.test.ViewTest;
import lombok.val;
import org.junit.jupiter.api.extension.AfterAllCallback;
import org.junit.jupiter.api.extension.AfterEachCallback;
import org.junit.jupiter.api.extension.BeforeAllCallback;
import org.junit.jupiter.api.extension.BeforeEachCallback;
import org.junit.jupiter.api.extension.ExtensionContext;

public class VaadinNavigationExtension implements
    BeforeEachCallback, BeforeAllCallback,
    AfterEachCallback, AfterAllCallback {

  @Override
  public void afterAll(ExtensionContext context) throws Exception {

  }

  @Override
  public void afterEach(ExtensionContext context) throws Exception {
    val method = context.getRequiredTestMethod();
    if (method.isAnnotationPresent(ViewTest.class)) {
      val viewTestAnnotation = method.getAnnotation(ViewTest.class);
      if (!Utilities.isDefault(viewTestAnnotation)) {
        val previous = Frames.resolveFrameStack(context).peek();
        if (previous != null) {
          previous.restore();
        }
      }
    }
  }

  @Override
  public void beforeAll(ExtensionContext context) throws Exception {

  }

  @Override
  public void beforeEach(ExtensionContext context) throws Exception {
    val method = context.getRequiredTestMethod();
    if (method.isAnnotationPresent(ViewTest.class)) {
      val viewTestAnnotation = method.getAnnotation(ViewTest.class);
      if (!Utilities.isDefault(viewTestAnnotation)) {
        val navigation = viewTestAnnotation.navigateTo();
        Frames.resolveCurrentFrame(context).navigateTo(navigation);
      }
    }
  }
}
