package io.sunshower.zephyr.ui.rmi;

import com.aire.ux.condensation.DocumentWriter;
import com.vaadin.flow.component.HasElement;
import com.vaadin.flow.component.page.PendingJavaScriptResult;
import io.sunshower.arcus.reflect.Reflect;
import java.io.IOException;
import java.io.Serializable;
import java.util.Collection;
import lombok.NonNull;
import lombok.val;

final class DefaultClientMethod<T> implements ClientMethod<T> {

  private final String name;
  private final String expression;
  private final DocumentWriter writer;
  private final Class<?>[] argumentTypes;

  public DefaultClientMethod(
      @NonNull DocumentWriter writer,
      @NonNull String name,
      @NonNull String expression,
      @NonNull Class<?>[] argumentTypes) {
    this.name = name;
    this.writer = writer;
    this.expression = expression;
    this.argumentTypes = argumentTypes;
  }

  @Override
  public PendingJavaScriptResult invoke(HasElement element, Object... params) {
    return element.getElement().callJsFunction(name, (Serializable[]) writeAll(params));
  }

  @SuppressWarnings({"unchecked", "rawtypes"})
  private String[] writeAll(Object[] params) {
    val result = new String[params.length];
    for (int i = 0; i < params.length; i++) {
      val param = params[i];
      try {
        if (isCollection(param)) {
          result[i] = writer.writeAll((Class) param.getClass(), (Collection) param);

        } else {
          result[i] = writer.write((Class) param.getClass(), param);
        }
      } catch (IOException ex) {
        throw new IllegalStateException(
            String.format(
                "Error: encountered serialization issue (%s) while trying to serialize type '%s' (position: '%d', function: '%s')",
                ex.getMessage(), param.getClass(), i, name));
      }
    }
    return result;
  }

  private boolean isCollection(Object param) {
    return Collection.class.isAssignableFrom(param.getClass());
  }

  private void validate(Object[] params) {
    if (params.length != argumentTypes.length) {
      throw new IllegalArgumentException(
          String.format(
              "Error: argument count mismatch.  Expected %d params, got %d",
              argumentTypes.length, params.length));
    }

    for (int i = 0; i < params.length; i++) {
      val param = params[i];
      if (param == null) {
        continue;
      }
      val actualType = param.getClass();
      val expectedType = argumentTypes[i];
      if (!Reflect.isCompatible(expectedType, actualType)) {
        throw new IllegalArgumentException(
            String.format(
                "Error: expected parameter of type '%s', got a parameter of type '%s' (value: '%s')",
                expectedType, actualType, param));
      }
    }
  }
}
