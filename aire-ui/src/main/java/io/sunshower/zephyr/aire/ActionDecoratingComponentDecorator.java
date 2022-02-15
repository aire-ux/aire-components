package io.sunshower.zephyr.aire;

import com.aire.ux.core.decorators.ComponentDecorator;
import com.vaadin.flow.component.HasElement;
import lombok.NonNull;

public class ActionDecoratingComponentDecorator implements ComponentDecorator {



  public ActionDecoratingComponentDecorator() {

    System.out.println("Inss");
  }

  @Override
  public void onComponentEntered(@NonNull HasElement component) {
//    ComponentDecorator.super.onComponentEntered(component);
    System.out.println("Entered" + component);
  }

  @Override
  public void onComponentExited(@NonNull HasElement component) {
    System.out.println("Exited" + component);
//    ComponentDecorator.super.onComponentExited(component);
  }
}
