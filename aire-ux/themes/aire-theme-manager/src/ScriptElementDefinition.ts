import {ScriptDefinition, Source} from "./Theme";
import AireThemeManager, {Registration} from "./AireThemeManager";
import {LocalInlineScriptInstaller, ScriptInstaller} from "./ScriptInstallers";

export class ScriptElementDefinition {


  static installers: Map</**
   * the source of the script to use
   */
      Source,
      /**
       * a typed script installer constructor
       */
      {
        new(manager: AireThemeManager): ScriptInstaller
      }>


  static initialize() {
    const bySource = new Map();
    bySource.set('inline', LocalInlineScriptInstaller);
    ScriptElementDefinition.installers = bySource;
  }

  /**
   * create a new script element definition
   * @param properties the properties to use
   */
  constructor(
      public readonly properties: ScriptDefinition
  ) {

  }


  async install(themeManager: AireThemeManager): Promise<Registration> {
    return new LocalInlineScriptInstaller(
        themeManager
    ).install(this.properties);
  }
}

ScriptElementDefinition.initialize();