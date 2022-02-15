package io.sunshower.zephyr.aire;

import com.aire.ux.core.decorators.ComponentDecorator;
import com.vaadin.flow.component.HasElement;
import lombok.NonNull;

public class ActionDecoratingComponentDecorator implements ComponentDecorator {

  @Override
  public void onComponentEntered(@NonNull HasElement component) {}

  @Override
  public void onComponentExited(@NonNull HasElement component) {}
}
