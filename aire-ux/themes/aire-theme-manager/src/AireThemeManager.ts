/**
 * a style definition contains a name and a string
 */

export module Aire {

  interface ReplaceAware {
    replace(definition: string): Promise<ReplaceAware>;
  }


  export enum Mode {
    Global = 'Global',
    Constructable = 'constructable'
  }

  export type StyleMode = Mode | string;

  export type StyleDefinition = {
    id: string,
    url: string,
    mode: StyleMode

    /**
     * once we're in the DOM (somehow), what are we?
     */
    result: any
  }

  console.log("Theme manager loaded!");


  const styles: Map<StyleMode, Array<StyleDefinition>> = new Map();

  /**
   */
  styles.set(Mode.Global, []);
  styles.set(Mode.Constructable, []);

  /**
   * a list of styles that have been installed
   */
  // const installedGlobalStyles: Array<StyleDefinition> = [];


  // const installedConstructableStyles


  /**
   * install the list of styles into the DOM.
   * Each style is installed asynchronously
   * @param newStyles the styles to install
   */
  export function installStyles(newStyles: Array<StyleDefinition>): void {
    // installedGlobalStyles = styles;
    for (const style of newStyles) {


      let definition = styles.get(style.mode);
      if (!definition) {
        styles.set(style.mode, definition = []);
      }

      enqueueStyleInstallation(style).then(link => {
        console.log("installed " + link);
      });
    }
  }


  export function enqueueGlobalStyleDefinition(style: StyleDefinition): Promise<HTMLLinkElement> {
    console.info(`Enqueuing style for addition with id: ${style.id}, url: ${style.url}`);
    return new Promise<HTMLLinkElement>((resolve, reject) => {
      let link = document.createElement('link');
      link.id = style.id;
      link.href = style.url;
      link.rel = 'stylesheet';
      style.result = link;
      document.head.appendChild(link);
      link.onload = (e: Event) => {
        console.info(`Successfully added style at ${style.url}`);
        resolve(link);
      }
      link.onerror = (e: string | Event) => {
        console.warn(`Failed to add style at ${style.url}.  Reason: ${e}`)
        reject(link);
      }
    });
  }

  export function enqueueConstructableStyleDefinition(
      definition: StyleDefinition
  ): Promise<any> {
    const stylesheet = new CSSStyleSheet() as
        unknown as
        ReplaceAware & CSSStyleSheet;

    definition.result = stylesheet;
    if (definition.mode === Mode.Global) {
      const doc = document as unknown as {
        adoptedStyleSheets: Array<CSSStyleSheet>
      }
      doc.adoptedStyleSheets.push(stylesheet);
    }


    return stylesheet.replace(`@import url('${definition.url}')`)
        .then((success => {
              console.info("Successfully loaded stylesheet");
              // @ts-ignore
              styles.get(definition.mode).push(definition);
              return success;
            }),
            error => {
              console.warn(`Failed to load stylesheet.  Reason: ${error}`);
              return Promise.reject(error);
            })
  }

  /**
   * enqueue a stylesheet addition
   * @param style
   */
  export function enqueueStyleInstallation(style: StyleDefinition): Promise<any> {
    switch (style.mode) {
      case Mode.Global:
        return enqueueGlobalStyleDefinition(style);
      default:
        return enqueueConstructableStyleDefinition(style);
    }
  }


  function uninstallGlobalStyle(style: StyleDefinition): void {

    console.info(`Removing global style definition: ${style.url}`);
    let linkElement = document.getElementById(style.id);
    if (linkElement) {
      linkElement.remove();
    }
  }


  function uninstallConstructedStylesheet(
      definition: StyleDefinition
  ): void {

    let stack = [document.documentElement];
    while (stack.length) {
      let el = stack.pop();
      if (el) {
        const styleaware = el as unknown as {
          adoptedStyleSheets: Array<CSSStyleSheet>;
        }
        if (
            styleaware.adoptedStyleSheets &&
            styleaware
                .adoptedStyleSheets
                .indexOf(definition.result) != -1) {
          styleaware.adoptedStyleSheets =
              styleaware
                  .adoptedStyleSheets
                  .filter(style => style !== definition.result);
        }
        let children = el.children;
        for (let i = 0; i < children.length; i++) {
          let child = children.item(i);
          if (child) {
            stack.push(child as HTMLElement);
          }
        }
      }
    }

  }

  export function uninstallStyle(style: StyleDefinition) {
    switch (style.mode) {
      case Mode.Global:
        uninstallGlobalStyle(style);
      default: {
        uninstallConstructedStylesheet(style);
      }
    }
  }

  /**
   * @param toRemove the styles to remove from the page
   */
  export function uninstallStyles(toRemove: Array<StyleDefinition> | undefined = styles.get(Mode.Global)): void {
    if (toRemove) {
      for (const style of toRemove) {
        let idx = toRemove.indexOf(style);
        if (idx != -1) {
          uninstallStyle(style);
        } else {
          throw new Error(`Error: style definition with url ${style.url} is not managed by this theme-manager`);
        }
      }
    }
  }

}
(window as any).Aire = {
  ThemeManager: Aire
};
