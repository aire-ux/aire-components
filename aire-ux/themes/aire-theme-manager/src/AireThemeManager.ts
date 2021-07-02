/**
 * a style definition contains a name and a string
 */

export module Aire {

  /**
   * handle stylesheets
   */
  export type CustomEventHandler = (e: StyleEvent) => void;
  /**
   * type doesn't seem to be available in ES6 type definitions
   */
  export type ReplaceAware = CSSStyleSheet & {
    replace(definition: string): Promise<ReplaceAware>;
  }


  export enum EventType {
    CustomStyleAdded = 'aire-custom-style-added',
    CustomStyleRemoved = 'aire-custom-style-removed',
  }


  /**
   * determine if the theme manager should walk the dom and
   * force-remove adoptedStyles.
   */
  export const forceRemove = false;
  /**
   * the event detail for a custom event
   */
  export type EventDetail = {
    eventType: EventType;
    styleDefinition: StyleDefinition;
  }

  export type StyleValue = HTMLStyleElement | CSSStyleSheet;
  /**
   * style event type
   */
  export type StyleEvent = {
    detail: EventDetail;
  } & CustomEvent;

  /**
   * determine which region to install a style into
   */
  export enum Mode {
    Global = 'Global',
    Constructable = 'constructable'
  }

  /**
   * while unusued right now, it may be usedful
   * to store adopted styles in different regions
   */
  export type StyleMode = Mode | string;

  /**
   * the definition of a style to be managed
   * by the theme manager
   */
  export type StyleDefinition = {
    id: string
    url: string
    mode?: StyleMode

    /**
     * once we're in the DOM (somehow), what are we?
     */
    result?: StyleValue
  }

  console.info("Aire Theme manager loaded!");


  const styles: Map<StyleMode, Array<StyleDefinition>> = new Map();

  /**
   */
  styles.set(Mode.Global, []);
  styles.set(Mode.Constructable, []);


  /**
   * install the list of styles into the DOM.
   * Each style is installed asynchronously
   * @param newStyles the styles to install
   */
  export function installStyles(newStyles: Array<StyleDefinition>): void {
    // installedGlobalStyles = styles;
    for (const style of newStyles) {
      if (!style.mode) {
        style.mode = Mode.Global;
      }
      let definition = styles.get(style.mode);
      if (!definition && style.mode) {
        styles.set(style.mode, definition = [style]);
      } else if (definition) {
        definition.push(style);
      }

      enqueueStyleInstallation(style).then(link => {
        console.info("installed " + link);
      });
    }
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


  /**
   * attempt to remove constructed stylesheets from the dom
   * and shadow-dom elements.  Test this
   * @param definition
   * @private
   */
  function uninstallConstructedStylesheet(
      definition: StyleDefinition
  ): void {
    if (forceRemove) {
      let stack = [document.documentElement];
      while (stack.length) {
        let el = stack.pop();
        if (el) {
          const styleaware = el as unknown as {
            adoptedStyleSheets: Array<StyleValue>;
          }
          if (
              styleaware.adoptedStyleSheets &&
              styleaware
                  .adoptedStyleSheets
                  .indexOf(definition.result as StyleValue) != -1) {
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
    } else {
      const styleaware = document as unknown as {
        adoptedStyleSheets: Array<StyleValue>;
      }
      styleaware.adoptedStyleSheets =
          styleaware
              .adoptedStyleSheets
              .filter(s => definition.result !== s);
    }
  }


  export function uninstallStyle(style: StyleDefinition) {
    switch (style.mode) {
      case Mode.Global:
        uninstallGlobalStyle(style);
        break;
      default: {
        uninstallConstructedStylesheet(style);
      }
    }

    let event = new CustomEvent(
        EventType.CustomStyleRemoved, {
          detail: {
            eventType: EventType.CustomStyleRemoved,
            styleDefinition: style
          }
        });
    document.documentElement.dispatchEvent(event);
  }


  const registeredListeners: Array<{
    type: EventType,
    handler: CustomEventHandler
  }> = [];

  export function addStyleChangeListener(type: EventType, handler: CustomEventHandler) {
    registeredListeners.push({
      type: type,
      handler: handler
    });
    document
        .documentElement
        .addEventListener(type, handler as any);
  }

  /**
   *
   * @param type the type of the listener to remove
   * @param handler the handler
   */
  export function removeStyleChangeListener(
      type: EventType,
      handler: CustomEventHandler
  ) {
    document
        .documentElement
        .removeEventListener(type, handler as any);
  }

  /**
   * clean up listeners
   */
  export function clearListeners() {
    for (const listener of registeredListeners) {
      removeStyleChangeListener(listener.type, listener.handler);
    }
  }


  /**
   * @param toRemove the styles to remove from the page.
   * Defaults to the global styles
   */
  export function uninstallStyles(
      toRemove: Array<StyleDefinition> =
      styles.get(Mode.Global) || []): void {
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


  /**
   *
   * @param definition the definition to add
   * @private shouldn't be called directly
   */
  function enqueueConstructableStyleDefinition(
      definition: StyleDefinition
  ): Promise<any> {
    const stylesheet = new CSSStyleSheet() as ReplaceAware;
    return stylesheet
        .replace(`@import url('${definition.url}')`)
        .then((success => {
              definition.result = stylesheet;
              if (definition.mode === Mode.Constructable) {
                const doc = document as unknown as {
                  adoptedStyleSheets: Array<CSSStyleSheet>
                }
                doc.adoptedStyleSheets = [...doc.adoptedStyleSheets].concat(stylesheet);
              }
              let event = new CustomEvent(
                  EventType.CustomStyleAdded,
                  {
                    detail: {
                      eventType: EventType.CustomStyleAdded,
                      styleDefinition: stylesheet
                    }
                  });

              document
                  .documentElement
                  .dispatchEvent(event);
              console.info("Successfully loaded stylesheet");
              // @ts-ignore
              styles.get(definition.mode).push(definition);
              return success;
            }),
            error => {
              console.warn(`Failed to load stylesheet.  Reason: ${error}`);
              return Promise.reject(error);
            });
  }

  /**
   * @param style the style to install
   * @private should not be called externally
   */
  function enqueueGlobalStyleDefinition(
      style: StyleDefinition
  ): Promise<HTMLLinkElement> {
    console.info(`Enqueuing style for addition with id: ${style.id}, url: ${style.url}`);
    return new Promise<HTMLLinkElement>((
        resolve, reject) => {
      let link = document.createElement('link');
      link.id = style.id;
      link.href = style.url;
      link.rel = 'stylesheet';
      style.result = link;
      document.head.appendChild(link);

      let event = new CustomEvent(
          EventType.CustomStyleAdded, {
            detail: {
              eventType: EventType.CustomStyleAdded,
              styleDefinition: link
            }
          });
      document
          .documentElement
          .dispatchEvent(event);

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

}
(window as any).Aire = {
  ThemeManager: Aire
};
