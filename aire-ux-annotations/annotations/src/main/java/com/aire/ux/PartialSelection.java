package com.aire.ux;

import com.vaadin.flow.component.UI;
import java.util.Optional;
import java.util.function.Supplier;

public interface PartialSelection<T> {

  Optional<T> select(UserInterface ui, Supplier<UI> supplier);
}
