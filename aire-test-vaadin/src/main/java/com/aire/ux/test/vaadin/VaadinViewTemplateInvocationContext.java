package com.aire.ux.test.vaadin;

import com.aire.ux.test.Element;
import lombok.SneakyThrows;
import lombok.val;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.ParameterContext;
import org.junit.jupiter.api.extension.ParameterResolutionException;
import org.junit.jupiter.api.extension.ParameterResolver;
import org.junit.jupiter.api.extension.TestTemplateInvocationContext;

public class VaadinViewTemplateInvocationContext implements TestTemplateInvocationContext,
    ParameterResolver {


  @Override
  public boolean supportsParameter(ParameterContext parameterContext,
      ExtensionContext extensionContext) throws ParameterResolutionException {
    return parameterContext.getParameter().isAnnotationPresent(Element.class);
  }

  @Override
  @SneakyThrows
  public Object resolveParameter(ParameterContext parameterContext,
      ExtensionContext extensionContext) throws ParameterResolutionException {
    val parameter = parameterContext.getParameter();
    return parameter.getType().newInstance();
  }
}
