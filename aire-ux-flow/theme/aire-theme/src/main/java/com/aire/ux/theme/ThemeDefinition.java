package com.aire.ux.theme;

import static com.aire.ux.theme.ThemeUtilities.toScriptDefinition;
import static com.aire.ux.theme.ThemeUtilities.toStyleDefinition;

import com.aire.ux.Theme;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import lombok.Getter;
import lombok.val;

/** DTO for theme resources */
public class ThemeDefinition {

  /** the style definitions to load */
  @Getter private final List<StyleDefinition> styleDefinitions;

  /** the script definitions to load */
  @Getter private final List<ScriptDefinition> scriptDefinitions;

  public ThemeDefinition() {
    this(new ArrayList<>(), new ArrayList<>());
  }

  public ThemeDefinition(
      final Collection<StyleDefinition> styleDefinitions,
      final Collection<ScriptDefinition> scriptDefinitions) {
    this.styleDefinitions = new ArrayList<>(styleDefinitions);
    this.scriptDefinitions = new ArrayList<>(scriptDefinitions);
  }

  public ThemeDefinition(Theme target) {
    this(Collections.emptyList(), Collections.emptyList());
    for (val definition : target.getThemeResources()) {
      switch (definition.getType()) {
        case Javascript:
          addScriptDefinition(toScriptDefinition(definition));
          break;
        case Stylesheet:
          addStyleDefinition(toStyleDefinition(definition));
          break;
      }
    }
  }

  public void addStyleDefinition(StyleDefinition definition) {
    styleDefinitions.add(definition);
  }

  public void addScriptDefinition(ScriptDefinition definition) {
    scriptDefinitions.add(definition);
  }
}
