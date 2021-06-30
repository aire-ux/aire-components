package com.aire.ux.theme.bootstrap;

import com.aire.ux.control.Button;
import com.aire.ux.theme.BaseTheme;

public class BootstrapTheme extends BaseTheme {

  static final String ID = "aire-bootstrap";

  public BootstrapTheme() {
    super(ID, BootstrapTheme.class.getClassLoader());
    register(Button.class, BootstrapButtonDecorator.class);
  }


}
