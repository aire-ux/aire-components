package io.sunshower.zephyr.ui.components.beanform;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.HasValue;

@FunctionalInterface
public interface FieldComponentSupplier<T extends Component & HasValue<?, ?>> {

  public T create(FieldDescriptor descriptor, Field fieldAnnotation);
}
