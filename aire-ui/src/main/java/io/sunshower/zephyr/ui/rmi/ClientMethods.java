package io.sunshower.zephyr.ui.rmi;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.UI;
import io.sunshower.arcus.condensation.Condensation;
import io.sunshower.arcus.condensation.DocumentWriter;
import io.sunshower.zephyr.ZephyrApplication;
import io.sunshower.zephyr.ui.canvas.RemoteAction;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Supplier;
import lombok.NonNull;
import lombok.val;
import org.springframework.aop.support.AopUtils;

public final class ClientMethods {

  @NonNull private final DocumentWriter writer;
  @NonNull private final Condensation condensation;

  @NonNull private final Supplier<UI> uiSupplier;

  @NonNull private final Map<String, ClientMethod<?>> internmap;

  public ClientMethods(final Supplier<UI> uiSupplier) {
    this(uiSupplier, ZephyrApplication.getCondensation());
  }

  public ClientMethods(@NonNull Supplier<UI> uiSupplier, @NonNull Condensation condensation) {
    internmap = new HashMap<>();
    this.uiSupplier = uiSupplier;
    this.condensation = condensation;
    this.writer = condensation.getWriter();
  }

  public static ClientMethods withUiSupplier(Supplier<UI> supplier) {
    return new ClientMethods(supplier);
  }

  public static ClientMethods withUiSupplier(Component component) {
    return new ClientMethods(() -> component.getUI().orElseGet(UI::getCurrent));
  }

  @SuppressWarnings("unchecked")
  public <T extends Serializable> ClientMethod<T> get(String name, Class<?>... argumentTypes) {
    return (ClientMethod<T>)
        internmap.computeIfAbsent(
            name,
            k ->
                new DefaultClientMethod<T>(
                    writer,
                    name,
                    Parameters.constructExpression(name, argumentTypes),
                    argumentTypes));
  }

  public <T, U> RemoteAction<T, U> construct(
      Class<? extends RemoteAction<T, U>> action, Object... arguments) {
    try {
      val argumentTypes =
          collectAnnotatedArguments(action).orElseGet(() -> collectArgumentTypes(arguments));
      val ctor = action.getDeclaredConstructor(argumentTypes);
      val args = new Object[arguments.length + 1];
      args[0] = uiSupplier;
      System.arraycopy(arguments, 0, args, 1, arguments.length);
      return ctor.newInstance(args);
    } catch (Exception ex) {
      throw new IllegalStateException(ex);
    }
  }

  private <T, U> Optional<Class<?>[]> collectAnnotatedArguments(
      Class<? extends RemoteAction<T, U>> action) {
    val arguments = action.getDeclaredAnnotationsByType(Argument.class);
    if (arguments.length == 0) {
      return Optional.empty();
    }

    val result = new Class<?>[arguments.length + 1];
    result[0] = Supplier.class;
    for (int i = 0; i < arguments.length; i++) {
      val arg = arguments[i];
      if (arg.collection()) {
        result[i + 1] = arg.collectionType();
      } else {
        result[i + 1] = arg.type();
      }
    }
    return Optional.of(result);
  }

  private Class<?>[] collectArgumentTypes(Object[] arguments) {
    val result = new Class<?>[arguments.length + 1];
    result[0] = Supplier.class;
    for (int i = 0; i < arguments.length; i++) {
      val argument = Objects.requireNonNull(arguments[i], "argument must not be null");
      val actualType = AopUtils.getTargetClass(argument);
      result[i + 1] = actualType;
    }
    return result;
  }
}
