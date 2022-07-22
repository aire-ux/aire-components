package io.sunshower.zephyr.ui.components.beanform;

import com.vaadin.flow.component.checkbox.Checkbox;

public class CheckboxFieldComponentSupplier implements FieldComponentSupplier<Checkbox> {

  @Override
  public Checkbox create(FieldDescriptor descriptor, Field fieldAnnotation) {
    return new Checkbox();
  }
}
