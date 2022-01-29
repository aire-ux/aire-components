package io.sunshower.zephyr.ui.canvas.actions;

import com.vaadin.flow.component.UI;
import io.sunshower.zephyr.ui.canvas.Action;
import io.sunshower.zephyr.ui.rmi.ClientMethod;
import io.sunshower.zephyr.ui.rmi.ClientMethods;
import java.io.Serializable;
import java.util.function.Supplier;
import lombok.NonNull;

public abstract class AbstractClientMethodBoundAction implements Action {

  protected @NonNull
  final ClientMethod<Serializable> method;
  @NonNull
  private final String name;
  @NonNull
  private final Supplier<UI> supplier;

  @NonNull
  private final Class<? extends Serializable> returnType;



  protected AbstractClientMethodBoundAction(final String name,
      final Supplier<UI> supplier,
      final String methodName,
      final Class<? extends Serializable> returnType,
      final Class<?>... argumentTypes) {
    this.name = name;
    this.supplier = supplier;
    this.returnType = returnType;
    this.method = ClientMethods.withUiSupplier(supplier).get(methodName, argumentTypes);
  }

  @Override
  public String getKey() {
    return name;
  }


  public Supplier<UI> getSupplier() {
    return supplier;
  }

}
