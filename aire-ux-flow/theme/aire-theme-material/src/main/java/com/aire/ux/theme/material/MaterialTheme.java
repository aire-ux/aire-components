package com.aire.ux.theme.material;

import com.aire.ux.control.Button;
import com.aire.ux.theme.BaseTheme;
import com.aire.ux.theme.StylesheetResource;

@SuppressWarnings("PMD.UseProperClassLoader")
public class MaterialTheme extends BaseTheme {

  static final String ID = "aire-material";

  public MaterialTheme() {
    super(ID, MaterialTheme.class.getClassLoader());
    addResource(
        new StylesheetResource(
            "bootstrap.css", "META-INF/aire-theme/aire-bootstrap/bootstrap.min.css"));
    register(Button.class, MaterialButtonDecorator.class);
  }
}
