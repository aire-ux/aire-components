package com.aire.ux.theme.aire;

import com.aire.ux.theme.BaseTheme;
import com.aire.ux.theme.StylesheetResource;

@SuppressWarnings("PMD")
public class BaseAireTheme extends BaseTheme {


  public BaseAireTheme(String id) {
    super(id, BaseAireTheme.class.getClassLoader());
    addResource(
        new StylesheetResource(
            "aire-button-structure.css", "META-INF/aire-theme/structure/aire-button.css"));
  }



}
