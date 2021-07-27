import {ScriptDefinition, Source} from "./Theme";
import {AireThemeManager, Registration} from "./AireThemeManager";
import {
  LocalInlineScriptInstaller,
  RemoteInlineScriptInstaller,
  ScriptInstaller
} from "./ScriptInstallers";

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
    bySource.set('remote', RemoteInlineScriptInstaller);
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
    const props = this.properties,
        source = props.source,
        installer = ScriptElementDefinition.installers.get(source);
    if (installer) {
      return new installer(themeManager).install(props);
    }
    return Promise.reject("No script installer for source: " + source);

    // return new LocalInlineScriptInstaller(

    //     themeManager
    // ).install(this.properties);
  }
}

ScriptElementDefinition.initialize();