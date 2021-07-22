/**
 * a style definition contains a name and a string
 */
import {StyleElementDefinition} from "./StyleElementDefinition";
import {ScriptDefinition, ThemeDefinition} from "./Theme";
import {ScriptElementDefinition} from "./ScriptElementDefinition";

export interface Registration {
  remove() : void;
}

/**
 * AireThemeManager
 *
 * this component is responsible for client-side
 * theme-management
 *
 *
 *
 *
 */
export default class AireThemeManager {

  private theme: ThemeDefinition;

  private styleDefinitions: Array<CSSStyleSheet>
  private styleDefinitionsToRemove: Array<CSSStyleSheet>

  constructor() {
    this.styleDefinitions = [];
    this.styleDefinitionsToRemove = [];
  }


  public set currentTheme(definition: ThemeDefinition) {

    this.theme = definition;
    this.install(definition).then(theme => {

    })
  }

  public get currentTheme() : ThemeDefinition {
    if(!this.theme) {
      throw new Error("Error: no current theme set");
    }
    return this.theme;
  }

  public enqueueConstructableStyleSheet(definition: CSSStyleSheet) {
    this.styleDefinitions.push(definition);
  }


  private async install(definition: ThemeDefinition) : Promise<any> {
    const promises: Array<Promise<any>> = []
    this.enqueueScriptDefinitions(promises, definition.scripts);
    this.enqueueStyleInstallations(promises, definition.styles);
    return Promise.all(promises);
  }

  async addStyleDefinition(
      styleDefinition: StyleElementDefinition
  ) : Promise<Registration> {
    return styleDefinition.install(this);
  }

  enqueueConstructableStyleSheetForRemoval(styleSheet: CSSStyleSheet) {
    this.styleDefinitionsToRemove.push(styleSheet);
  }

  private enqueueScriptDefinitions(promises: Array<Promise<any>>, scripts: Array<ScriptDefinition>) {
    for(let script of scripts) {
      promises.push(new ScriptElementDefinition(script).install(this));
    }
  }


  private enqueueStyleInstallations(promises: Array<Promise<any>>, scripts: Array<ScriptDefinition>) {
    for(let script of scripts) {
      promises.push(new StyleElementDefinition(script).install(this));
    }
  }
}
