package io.sunshower.zephyr.ui.components.beanform;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.component.textfield.TextField;

public class StringFieldComponentSupplier implements FieldComponentSupplier<Component> {

  @Override
  public Component create(FieldDescriptor descriptor, Field fieldAnnotation) {
    if (descriptor.has(Password.class)) {
      return createPasswordField(descriptor, fieldAnnotation);
    } else {
      return createTextField(descriptor, fieldAnnotation);
    }
  }

  private Component createPasswordField(FieldDescriptor descriptor, Field fieldAnnotation) {
    return new PasswordField();
  }

  private Component createTextField(FieldDescriptor descriptor, Field fieldAnnotation) {
    return new TextField();
  }
}
