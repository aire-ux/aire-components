import {walkDom} from "./src/Utilities";
import {AireThemeManager} from "./src/AireThemeManager";

export * from './src/Theme';
export * from './src/Utilities';
export * from './src/ScriptElementDefinition'
export * from './src/StyleElementDefinition';
export * from './src/AireThemeManager'


declare global {
  interface Window {
    Aire: {
      ThemeManager: AireThemeManager
      Utilities: {
        walkDom: typeof walkDom
      }
    }
  }
}

(() => {
  window.Aire = window.Aire || {};
  window.Aire.Utilities = {
    walkDom: walkDom
  }
  if(!window.Aire.ThemeManager) {
    window.Aire.ThemeManager = new AireThemeManager();
  }
})()
