/**
 * a style definition contains a name and a string
 */
import {StyleElementDefinition} from "./StyleElementDefinition";
import {ScriptDefinition, ThemeDefinition} from "./Theme";
import {ScriptElementDefinition} from "./ScriptElementDefinition";
import {collectElements} from "./Utilities";

export interface Registration {
  remove(): void;
}

/**
 * AireThemeManager
 *
 * this component is responsible for client-side
 * theme-management
 */
export default class AireThemeManager {

  private theme: ThemeDefinition;

  private registrations: Registration[];
  private styleDefinitions: Array<CSSStyleSheet>
  private styleDefinitionsToRemove: Array<CSSStyleSheet>

  constructor() {
    this.styleDefinitions = [];
    this.styleDefinitionsToRemove = [];
  }

  /**
   * @param definition the theme definition to use
   */

  public set currentTheme(definition: ThemeDefinition) {
  }

  public installTheme(definition: ThemeDefinition): Promise<ThemeDefinition> {
    if (this.theme) {
      this.uninstall(this.theme);
    }
    this.theme = definition;
    return this.install(definition).then(registrations => {
      this.registrations = registrations;
      return this.theme;
    })
  }

  public removeTheme(): void {
    if (this.theme && this.registrations) {
      this.uninstall(this.theme)
    }
  }

  public get currentTheme(): ThemeDefinition {
    if (!this.theme) {
      throw new Error("Error: no current theme set");
    }
    return this.theme;
  }

  public enqueueConstructableStyleSheet(definition: CSSStyleSheet) {
    this.styleDefinitions.push(definition);
  }


  enqueueConstructableStyleSheetForRemoval(styleSheet: CSSStyleSheet) {
    this.styleDefinitionsToRemove.push(styleSheet);
  }

  private uninstall(theme: ThemeDefinition) {
    if (this.registrations) {
      for (const registration of this.registrations) {
        if (registration) {
          registration.remove();
        }
      }
      this.registrations.length = 0;
    }
  }

  private async install(definition: ThemeDefinition): Promise<Registration[]> {
    const promises: Array<Promise<Registration>> = []
    if (definition.scripts) {
      this.enqueueScriptDefinitions(promises, definition.scripts);
    }
    if (definition.styles) {
      this.enqueueStyleInstallations(promises, definition.styles);
    }
    return Promise.all(promises).then(regs => {
      this.applyStyles()
      return regs;
    })
  }

  async addStyleDefinition(
      styleDefinition: StyleElementDefinition
  ): Promise<Registration> {
    return styleDefinition.install(this);
  }

  private enqueueScriptDefinitions(promises: Array<Promise<Registration>>, scripts: Array<ScriptDefinition>) {
    for (let script of scripts) {
      promises.push(new ScriptElementDefinition(script).install(this));
    }
  }


  private enqueueStyleInstallations(promises: Array<Promise<Registration>>, scripts: Array<ScriptDefinition>) {
    for (let script of scripts) {
      promises.push(new StyleElementDefinition(script).install(this));
    }
  }

  private applyStyles() {
    if (this.theme && this.theme.installationInstructions) {
      let elements = collectElements(this.theme.installationInstructions),
          styleDefinitions = this.styleDefinitions;
      if (
          elements &&
          elements.length &&
          styleDefinitions &&
          styleDefinitions.length
      ) {
        for (const element of elements) {
          if (element.shadowRoot) {
            const sr = element.shadowRoot as ShadowRoot & {
              adoptedStyleSheets: Array<CSSStyleSheet> | null | undefined;
            }
            if (sr.adoptedStyleSheets) {
              for (const style of styleDefinitions) {
                sr.adoptedStyleSheets = [...sr.adoptedStyleSheets, style]
              }
            }
          }
        }
      }
    }
  }

}
