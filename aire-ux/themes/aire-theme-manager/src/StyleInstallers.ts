import {AireThemeManager} from "./AireThemeManager";
import {
  ErrorStyleRegistration,
  LinkStyleRegistration,
  StyleInstaller,
  StyleRegistration
} from "./StyleElementDefinition";
import {constructStyleSheetFrom} from "./Utilities";
import {StyleDefinition} from "./Theme";


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

  install(properties: StyleDefinition): Promise<StyleRegistration> {
    const styleElement = document.createElement('style');
    styleElement.textContent = properties.content;
    return new Promise((resolve) => {
      document.head.append(styleElement);
      resolve(new LinkStyleRegistration(styleElement));
    });
  }
}

export type ConstructableStyleSheetAware = {
  adoptedStyleSheets?: Array<CSSStyleSheet>
}


/**
 * creates a constructablestylesheet
 */
export class RemoteConstructableStyleInstaller implements StyleInstaller {


  constructor(
      readonly manager: AireThemeManager
  ) {
  }

  install(
      properties: StyleDefinition
  ): Promise<StyleRegistration> {

    return constructStyleSheetFrom(properties)
        .then(styleSheet => {
          let manager = this.manager;
          manager.enqueueConstructableStyleSheet(styleSheet);
          return new ConstructableStyleRegistration(manager, styleSheet);
        });
  }

}


class ConstructableStyleRegistration implements StyleRegistration {
  constructor(readonly manager: AireThemeManager, readonly styleSheet: CSSStyleSheet) {
  }

  remove(): void {
    this.manager.enqueueConstructableStyleSheetForRemoval(this.styleSheet);
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

  install(properties: StyleDefinition): Promise<StyleRegistration> {
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
