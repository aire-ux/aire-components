package com.aire.ux.theme.aire;

import com.aire.ux.theme.StylesheetResource;

public class AireLightTheme extends BaseAireTheme {
  static final String ID = "aire-light-theme";

  public AireLightTheme() {
    super(ID);
    addResource(
        new StylesheetResource("aire-button.css", "META-INF/aire-theme/aire/aire-button.css"));
  }
}
