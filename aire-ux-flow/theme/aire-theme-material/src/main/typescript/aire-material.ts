import * as mdb from 'mdb-ui-kit'
import {Aire} from "@aire-ux/aire-theme-manager";

declare global {
  interface Window {
    Aire: any
  }
}

document.addEventListener(
    'DOMContentLoaded',
    () => {
      registerListeners();
    });

export function registerListeners() {
  // window.Aire.ThemeManager.addStyleChangeListener(Aire.EventType.ThemeInstalled, registerEffects)
}

export function registerEffects() {

  window.Aire.ThemeManager.walkDom((element: Aire.StyleAware) => {
    if(element.hasAttribute('aire-ripple')) {
      new mdb.Ripple()

    }
  });



}