package com.aire.ux.theme;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * DTO for theme resources
 */
public class ThemeDefinition {

  /**
   * the style definitions to load
   */
  private final List<StyleDefinition> styleDefinitions;

  /**
   * the script definitions to load
   */
  private final List<ScriptDefinition> scriptDefinitions;


  public ThemeDefinition() {
    this(new ArrayList<>(), new ArrayList<>());
  }

  public ThemeDefinition(
      final Collection<StyleDefinition> styleDefinitions,
      final Collection<ScriptDefinition> scriptDefinitions) {
    this.styleDefinitions = new ArrayList<>(styleDefinitions);
    this.scriptDefinitions = new ArrayList<>(scriptDefinitions);
  }


  public void addStyleDefinition(StyleDefinition definition) {
    styleDefinitions.add(definition);
  }

  public void addScriptDefinition(ScriptDefinition definition) {
    scriptDefinitions.add(definition);
  }

}
