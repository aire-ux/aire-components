package com.aire.ux.theme.aire;

import com.aire.ux.theme.StylesheetResource;

public class AireDarkTheme extends BaseAireTheme {

  static final String ID = "aire-dark-theme";

  public AireDarkTheme() {
    super(ID);
    addResource(
        new StylesheetResource("aire-button.css", "META-INF/aire-theme/aire-dark/aire-button.css"));
  }
}
