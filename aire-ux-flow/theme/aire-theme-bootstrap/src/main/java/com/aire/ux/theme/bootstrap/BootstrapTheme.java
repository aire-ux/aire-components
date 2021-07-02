package com.aire.ux.theme.bootstrap;

import com.aire.ux.control.Button;
import com.aire.ux.theme.BaseTheme;
import com.aire.ux.theme.StylesheetResource;

@SuppressWarnings("PMD.UseProperClassLoader")
public class BootstrapTheme extends BaseTheme {

  static final String ID = "aire-bootstrap";

  public BootstrapTheme() {
    super(ID, BootstrapTheme.class.getClassLoader());
    addResource(
        new StylesheetResource(
            "bootstrap.css", "META-INF/aire-theme/aire-bootstrap/bootstrap.min.css"));
    register(Button.class, BootstrapButtonDecorator.class);
  }
}
