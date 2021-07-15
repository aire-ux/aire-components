package com.aire.ux.theme;

import com.aire.ux.ThemeResource;

public class ThemeUtilities {


  public static ScriptDefinition toScriptDefinition(ThemeResource resource) {
    return new ScriptDefinition(resource.getLocation());
  }

  public static StyleDefinition toStyleDefinition(ThemeResource resource) {
    return new StyleDefinition(resource.getLocation());

  }

}
