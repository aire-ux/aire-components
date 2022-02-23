package io.sunshower.zephyr.ui.canvas.actions;

import com.vaadin.flow.component.UI;
import io.sunshower.zephyr.ui.canvas.CanvasAction;
import io.sunshower.zephyr.ui.rmi.ClientMethod;
import io.sunshower.zephyr.ui.rmi.ClientMethods;
import java.io.Serializable;
import java.util.function.Supplier;
import lombok.NonNull;

public abstract class AbstractClientMethodBoundAction<T> implements CanvasAction<T> {

  protected @NonNull final ClientMethod<Serializable> method;
  @NonNull private final String name;
  @NonNull private final Supplier<UI> supplier;

  @NonNull private final Class<? extends Serializable> returnType;

  protected AbstractClientMethodBoundAction(
      final String name,
      final Supplier<UI> supplier,
      final String methodName,
      final Class<? extends Serializable> returnType,
      final Class<?>... argumentTypes) {
    this.name = name;
    this.supplier = supplier;
    this.returnType = returnType;
    this.method = ClientMethods.withUiSupplier(supplier).get(methodName, argumentTypes);
  }

  protected AbstractClientMethodBoundAction(
      final String name,
      final String methodName,
      final Class<? extends Serializable> returnType,
      final Class<?>... argumentTypes) {
    this(name, UI::getCurrent, methodName, returnType, argumentTypes);
  }

  @Override
  public String getKey() {
    return name;
  }

  public Supplier<UI> getSupplier() {
    return supplier;
  }
}
