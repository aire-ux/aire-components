package com.aire.ux;

import com.vaadin.flow.component.HasElement;
import com.vaadin.flow.component.UI;
import java.util.Optional;
import java.util.function.Supplier;

public interface PartialSelection<T> {

  boolean isHostedBy(Class<?> type);

  Optional<T> select(UserInterface ui, Supplier<UI> supplier);

  Optional<T> select(HasElement component, UserInterface userInterface);
}
