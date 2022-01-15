package com.aire.ux.theme;

import static com.aire.ux.theme.ThemeUtilities.toScriptDefinition;
import static com.aire.ux.theme.ThemeUtilities.toStyleDefinition;

import com.aire.ux.Theme;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import lombok.Getter;
import lombok.val;

/** DTO for theme resources */
@SuppressFBWarnings
public class ThemeDefinition {

  /** the style definitions to load */
  @Getter private final List<StyleDefinition> styles;

  /** the script definitions to load */
  @Getter private final List<ScriptDefinition> scripts;

  public ThemeDefinition() {
    this(new ArrayList<>(), new ArrayList<>());
  }

  public ThemeDefinition(
      final Collection<StyleDefinition> styles, final Collection<ScriptDefinition> scripts) {
    this.styles = new ArrayList<>(styles);
    this.scripts = new ArrayList<>(scripts);
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
    styles.add(definition);
  }

  public void addScriptDefinition(ScriptDefinition definition) {
    scripts.add(definition);
  }
}
