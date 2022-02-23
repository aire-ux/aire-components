package com.aire.ux.test;

import com.aire.ux.ext.ExtensionRegistry;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.di.Instantiator;
import com.vaadin.flow.server.VaadinServlet;
import java.lang.reflect.AnnotatedElement;
import java.util.Optional;
import lombok.val;
import org.junit.jupiter.api.extension.AfterAllCallback;
import org.junit.jupiter.api.extension.AfterEachCallback;
import org.junit.jupiter.api.extension.BeforeAllCallback;
import org.junit.jupiter.api.extension.BeforeEachCallback;
import org.junit.jupiter.api.extension.Extension;
import org.junit.jupiter.api.extension.ExtensionContext;

public class RegisterComponentExtension
    implements Extension,
        BeforeAllCallback,
        AfterAllCallback,
        BeforeEachCallback,
        AfterEachCallback {

  @Override
  public void afterAll(ExtensionContext context) throws Exception {}

  @Override
  public void afterEach(ExtensionContext context) throws Exception {}

  @Override
  public void beforeAll(ExtensionContext context) throws Exception {
    context.getTestClass().ifPresent(this::defineExtensions);
  }

  @SuppressWarnings("unchecked")
  private void defineExtensions(AnnotatedElement type) {
    val extensions = type.getAnnotationsByType(RegisterExtension.class);
    val instantiator = getInstantiator();
    val registry = instantiator.getOrCreate(ExtensionRegistry.class);
    for (val extension : extensions) {
      registry.defineExtension(extension.value());
    }
  }

  private Instantiator getInstantiator() {
    val instantiator =
        Optional.ofNullable(Instantiator.get(UI.getCurrent()))
            .orElseGet(() -> VaadinServlet.getCurrent().getService().getInstantiator());
    if (instantiator == null) {
      throw new IllegalStateException(
          "AireTest is probably misconfigured--could not locate an instantiator");
    }
    return instantiator;
  }

  @Override
  public void beforeEach(ExtensionContext context) throws Exception {}
}
