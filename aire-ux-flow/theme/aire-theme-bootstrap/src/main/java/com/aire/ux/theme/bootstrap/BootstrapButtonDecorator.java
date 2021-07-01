package com.aire.ux.theme.bootstrap;

import com.aire.ux.control.Button;
import com.aire.ux.theme.Decorator;

public class BootstrapButtonDecorator implements Decorator<Button> {

  @Override
  public Class<Button> getTarget() {
    return Button.class;
  }

  @Override
  public void decorate(Button value) {
    value.getElement().setAttribute("type", "button");
    value.getElement().setAttribute("classes", "btn btn-primary");
    //    value.setClassName("btn");
    //    value.setClassName("btn-outline-primary");
  }
}
