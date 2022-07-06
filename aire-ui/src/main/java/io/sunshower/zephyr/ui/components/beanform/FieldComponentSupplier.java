package io.sunshower.zephyr.ui.components.beanform;

import com.vaadin.flow.component.Component;

@FunctionalInterface
public interface FieldComponentSupplier<T extends Component> {

  public T create(FieldDescriptor descriptor, Field fieldAnnotation);
}
