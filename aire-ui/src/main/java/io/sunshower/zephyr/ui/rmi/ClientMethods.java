package io.sunshower.zephyr.ui.rmi;

import com.aire.ux.condensation.Condensation;
import com.aire.ux.condensation.DocumentWriter;
import com.vaadin.flow.component.UI;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;
import lombok.NonNull;

public final class ClientMethods {

  @NonNull private final DocumentWriter writer;
  @NonNull private final Condensation condensation;

  @NonNull private final Supplier<UI> uiSupplier;

  @NonNull private final Map<String, ClientMethod<?>> internmap;

  public ClientMethods(final Supplier<UI> uiSupplier) {
    this(uiSupplier, Condensation.create("json"));
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
}
