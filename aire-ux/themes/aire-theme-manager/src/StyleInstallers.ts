import AireThemeManager from "./AireThemeManager";
import {
  ErrorStyleRegistration,
  LinkStyleRegistration,
  PageStyleDefinitionProperties,
  StyleInstaller,
  StyleRegistration
} from "./PageStyleDefinition";
import {loadText, walkDom} from "./Utilities";


/**
 * installs an inline page style,
 * e.g.
 * <style type="text/css">
 *   content
 * </style>
 *
 */
export class InlinePageStyleInstaller implements StyleInstaller {

  constructor(
      readonly manager: AireThemeManager
  ) {
  }

  install(properties: PageStyleDefinitionProperties): Promise<StyleRegistration> {
    const styleElement = document.createElement('style');
    styleElement.textContent = properties.content;
    return new Promise((resolve) => {
      document.head.append(styleElement);
      resolve(new LinkStyleRegistration(styleElement));
    });
  }
}

type ConstructableStyleSheetAware = {
  adoptedStyleSheets?: Array<CSSStyleSheet>
}


class ConstructableStyleRegistration implements StyleRegistration {
  constructor(
      readonly w: ConstructableStyleSheetAware,
      readonly stylesheet: CSSStyleSheet
  ) {

  }

  remove(): void {
    if (this.w.adoptedStyleSheets) {
      this.w.adoptedStyleSheets =
          this.w.adoptedStyleSheets
              .filter(sheet => sheet !== this.stylesheet);
    }
  }

}

/**
 * creates a constructablestylesheet
 */
export class RemoteConstructableStyleInstaller implements StyleInstaller {

  constructor(
      readonly manager: AireThemeManager
  ) {
  }

  install(properties: PageStyleDefinitionProperties): Promise<StyleRegistration> {
    const textLoader = properties.urlLoader ?? loadText;
    return new Promise((resolve, reject) => {
      textLoader(properties.content, 'GET').then(
          styleDefinition => {
            const stylesheet = new CSSStyleSheet() as CSSStyleSheet & {
                  replace(definition: string): Promise<StyleSheet>
                },
                w = document as any as ConstructableStyleSheetAware;
            stylesheet.replace(styleDefinition).then(success => {
              resolve(new ConstructableStyleRegistration(w, stylesheet))
            })
            if (w.adoptedStyleSheets) {
              w.adoptedStyleSheets = [
                ...w.adoptedStyleSheets,
                stylesheet as any as CSSStyleSheet
              ]
            }

            if(properties.forceAdopt) {
              walkDom(
                  document.documentElement,
                  e => {
                    if(e.adoptedStyleSheets) {
                      adoptS

                    }
                  },
                  e => {
                    return e as ConstructableStyleSheetAware
                  }
              )

            }

          });
    });
  }
}


/**
 * installs a global page style to the document's head, e.g.
 *
 * <link rel="stylesheet" type="text/css" href="properties.conent"></link>
 */
export class GlobalPageStyleInstaller implements StyleInstaller {
  constructor(
      readonly manager: AireThemeManager
  ) {
  }

  install(properties: PageStyleDefinitionProperties): Promise<StyleRegistration> {
    const styleElement = document.createElement('link');
    styleElement.rel = 'stylesheet';
    styleElement.type = 'text/css';
    styleElement.href = properties.content;
    return new Promise((resolve, reject) => {
      document.head.append(styleElement);
      styleElement.onload = (event: Event) => {
        resolve(new LinkStyleRegistration(styleElement));
      }
      styleElement.onerror = (event: Event | string) => {
        reject(new ErrorStyleRegistration(event, styleElement));
      }
    });
  }
}
