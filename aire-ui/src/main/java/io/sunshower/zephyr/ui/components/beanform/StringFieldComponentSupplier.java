package io.sunshower.zephyr.ui.components.beanform;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.HasValue;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.component.textfield.TextField;

@SuppressWarnings("unchecked")
public class StringFieldComponentSupplier<T extends Component & HasValue<?, ?>>
    implements FieldComponentSupplier<T> {

  @Override
  public T create(FieldDescriptor descriptor, Field fieldAnnotation) {
    if (descriptor.has(Password.class)) {
      return createPasswordField(descriptor, fieldAnnotation);
    } else {
      return createTextField(descriptor, fieldAnnotation);
    }
  }

  private T createPasswordField(FieldDescriptor descriptor, Field fieldAnnotation) {
    return (T) new PasswordField();
  }

  private T createTextField(FieldDescriptor descriptor, Field fieldAnnotation) {
    return (T) new TextField();
  }
}
