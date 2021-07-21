/**
 * a style definition contains a name and a string
 */
import {PageStyleDefinition} from "./PageStyleDefinition";

export interface Registration {
  remove() : void;
}

export default class AireThemeManager {

  async addStyleDefinition(
      styleDefinition: PageStyleDefinition
  ) : Promise<Registration> {
    return styleDefinition.install(this);
  }
}

// export module Aire {
//
//   /**
//    * handle stylesheets
//    */
//   export type CustomEventHandler = (e: StyleEvent) => void;
//   /**
//    * type doesn't seem to be available in ES6 type definitions
//    */
//   export type ReplaceAware = CSSStyleSheet & {
//     replace(definition: string): Promise<ReplaceAware>;
//   }
//
//
//   export enum EventType {
//
//     /**
//      *
//      */
//     ThemeInstalling = 'aire-theme-installing',
//
//     /**
//      *
//      */
//     ThemeInstalled = 'aire-theme-installed',
//     /**
//      * fired after a theme is removed--can p
//      */
//     ThemeUninstalled = 'aire-theme-uninstalled',
//
//     /**
//      * fired before a theme is removed, providing a chance for themes
//      * to clean up after themselves
//      */
//     ThemeUninstalling = 'aire-theme-uninstalled',
//     /**
//      *
//      */
//     ScriptAdded = 'aire-script-added',
//
//     ScriptRemoved = 'aire-script-removed',
//
//     /**
//      *
//      */
//     CustomStyleAdded = 'aire-custom-style-added',
//
//     /**
//      *
//      */
//     CustomStyleRemoved = 'aire-custom-style-removed',
//   }
//
//
//   /**
//    * determine if the theme manager should walk the dom and
//    * force-remove adoptedStyles.
//    */
//   export let forceRemove = false;
//   /**
//    * the event detail for a custom event
//    */
//   export type EventDetail = {
//     eventType: EventType;
//     styleDefinition: StyleDefinition;
//   }
//
//   export type StyleValue = HTMLStyleElement | CSSStyleSheet;
//   /**
//    * style event type
//    */
//   export type StyleEvent = {
//     detail: EventDetail;
//   } & CustomEvent;
//
//   /**
//    * determine which region to install a style into
//    */
//   export enum Mode {
//     Global = 'Global',
//     Constructable = 'Constructable'
//   }
//
//   /**
//    * while unusued right now, it may be usedful
//    * to store adopted styles in different regions
//    */
//   export type StyleMode = Mode | string;
//
//   export type ThemeResource = {
//
//     /**
//      * the order in which a theme-resource should be installed--lower = higher precedence
//      * default is 0 (no precedence)
//      */
//     order: number;
//     /**
//      * the ID of this resource.  Will be used for the actual ID of
//      * the tag
//      */
//     id?: string;
//     /**
//      * the URL to load this resource from
//      */
//     url?: string;
//
//     /**
//      * load this resource inline
//      */
//     content?: string
//
//     /**
//      * specify the integrity of this resource
//      */
//     integrity?: string
//   }
//   /**
//    * the definition of a style to be managed
//    * by the theme manager
//    */
//   export type StyleDefinition = {
//     mode?: StyleMode
//
//     /**
//      * once we're in the DOM (somehow), what are we?
//      */
//     result?: StyleValue
//   } & ThemeResource;
//
//
//   export type ScriptDefinition = {
//     async: boolean;
//     defer: boolean;
//     contents?: string
//   } & ThemeResource
//
//   export type Theme = {
//
//     /**
//      * the style definitions for this theme
//      */
//     styleDefinitions: Array<StyleDefinition>;
//
//     /**
//      * the scripts to install with this theme
//      */
//     scriptDefinitions: Array<ScriptDefinition>;
//
//   }
//
//   console.info("Aire Theme manager loaded!");
//
//
//   /**
//    *
//    * @private the scripts currently loaded
//    */
//   const scripts: Array<ScriptDefinition> = [];
//
//   const styles: Map<StyleMode, Array<StyleDefinition>> = new Map();
//
//   /**
//    */
//   styles.set(Mode.Global, []);
//   styles.set(Mode.Constructable, []);
//
//
//   let currentTheme: Theme;
//
//
//   /**
//    * convenience method for dispatching an event
//    * @param event the event to dispatch
//    */
//   export function fireEvent(event: CustomEvent) {
//     document.documentElement.dispatchEvent(event);
//   }
//
//   export function installTheme(theme: Theme): Promise<any> {
//     if (currentTheme) {
//       removeTheme();
//     }
//     const previousTheme = currentTheme;
//     fireEvent(new CustomEvent(EventType.ThemeInstalling, {
//       detail: {
//         oldTheme: previousTheme,
//         newTheme: theme,
//       }
//     }));
//     return Promise.all([
//       installScripts(theme.scriptDefinitions),
//       installStyles(theme.styleDefinitions)
//     ]).then(_ => {
//       currentTheme = theme;
//       fireEvent(new CustomEvent(EventType.ThemeInstalled, {
//         detail: {
//           oldTheme: previousTheme,
//           newTheme: currentTheme,
//         }
//       }));
//     })
//   }
//
//
//   export function removeTheme() {
//     fireEvent(new CustomEvent(EventType.ThemeUninstalling, {
//       detail: {
//         theme: currentTheme,
//       }
//     }));
//
//     uninstallStyles();
//     uninstallScripts();
//     fireEvent(new CustomEvent(EventType.ThemeInstalled, {
//       detail: {
//         theme: currentTheme,
//       }
//     }));
//   }
//
//   function uninstallScripts() {
//     for (let script of scripts) {
//       if (script.id) {
//         let scriptTag = document.getElementById(script.id);
//         if (scriptTag) {
//           scriptTag.remove();
//         }
//       }
//     }
//   }
//
//
//   function installScripts(scriptDefinitions: Array<ScriptDefinition>): Promise<any[]> {
//     const promises: Array<Promise<any>> = [];
//     for (let script of scriptDefinitions) {
//       promises.push(enqueueGlobalScriptDefinition(script).then(_ => scripts.push(script)));
//     }
//     return Promise.all(promises);
//   }
//
//   /**
//    * install the list of styles into the DOM.
//    * Each style is installed asynchronously
//    * @param newStyles the styles to install
//    */
//   export function installStyles(newStyles: Array<StyleDefinition>): Promise<any[]> {
//     // installedGlobalStyles = styles;
//     const promises: Array<Promise<any>> = []
//     for (const style of newStyles) {
//       if (!style.mode) {
//         style.mode = Mode.Global;
//       }
//       let definition = styles.get(style.mode);
//       if (!definition && style.mode) {
//         styles.set(style.mode, definition = [style]);
//       } else if (definition) {
//         definition.push(style);
//       }
//       promises.push(enqueueStyleInstallation(style));
//     }
//     return Promise.all(promises);
//   }
//
//
//   /**
//    * enqueue a stylesheet addition
//    * @param style
//    */
//   export function enqueueStyleInstallation(style: StyleDefinition): Promise<any> {
//     switch (style.mode) {
//       case Mode.Global:
//         return enqueueGlobalStyleDefinition(style);
//       default:
//         return enqueueConstructableStyleDefinition(style);
//     }
//   }
//
//
//   function uninstallGlobalStyle(style: StyleDefinition): void {
//
//     console.info(`Removing global style definition: ${style.url}`);
//     if (style.id) {
//       let linkElement = document.getElementById(style.id);
//       if (linkElement) {
//         linkElement.remove();
//       }
//     }
//   }
//
//
//   export type StyleAware = {
//     adoptedStyleSheets: Array<StyleValue>
//   } & HTMLElement;
//
//   export function walkDom(f: (el: StyleAware) => void) {
//
//     let stack = [document.documentElement];
//     while (stack.length) {
//       let el = stack.pop();
//       if (el) {
//         const styleAware = el as StyleAware;
//         f(styleAware);
//         let children = el.children;
//         for (let i = 0; i < children.length; i++) {
//           let child = children.item(i);
//           if (child) {
//             stack.push(child as HTMLElement);
//           }
//         }
//       }
//
//     }
//   }
//
//   /**
//    * attempt to remove constructed stylesheets from the dom
//    * and shadow-dom elements.  Test this
//    * @param definition
//    * @private
//    */
//   function uninstallConstructedStylesheet(
//       definition: StyleDefinition
//   ): void {
//
//     if (forceRemove) {
//       walkDom((styleaware: StyleAware): void => {
//         if (
//             styleaware.adoptedStyleSheets &&
//             styleaware
//                 .adoptedStyleSheets
//                 .indexOf(definition.result as StyleValue) != -1) {
//           styleaware.adoptedStyleSheets =
//               styleaware
//                   .adoptedStyleSheets
//                   .filter(style => style !== definition.result);
//         }
//       });
//     } else {
//       const styleAware = document as unknown as StyleAware;
//       styleAware.adoptedStyleSheets =
//           styleAware
//               .adoptedStyleSheets
//               .filter(s => definition.result !== s);
//     }
//   }
//
//
//   export function uninstallStyle(style: StyleDefinition) {
//     switch (style.mode) {
//       case Mode.Global:
//         uninstallGlobalStyle(style);
//         break;
//       default: {
//         uninstallConstructedStylesheet(style);
//       }
//     }
//
//     let event = new CustomEvent(
//         EventType.CustomStyleRemoved, {
//           detail: {
//             eventType: EventType.CustomStyleRemoved,
//             styleDefinition: style
//           }
//         });
//     document
//         .documentElement
//         .dispatchEvent(event);
//   }
//
//
//   const registeredListeners: Array<{
//     type: EventType,
//     handler: CustomEventHandler
//   }> = [];
//
//   export function addStyleChangeListener(type: EventType, handler: CustomEventHandler) {
//     registeredListeners.push({
//       type: type,
//       handler: handler
//     });
//     document
//         .documentElement
//         .addEventListener(type, handler as any);
//   }
//
//   /**
//    *
//    * @param type the type of the listener to remove
//    * @param handler the handler
//    */
//   export function removeStyleChangeListener(
//       type: EventType,
//       handler: CustomEventHandler
//   ) {
//     document
//         .documentElement
//         .removeEventListener(type, handler as any);
//   }
//
//   /**
//    * clean up listeners
//    */
//   export function clearListeners() {
//     for (const listener of registeredListeners) {
//       removeStyleChangeListener(listener.type, listener.handler);
//     }
//   }
//
//
//   /**
//    * @param toRemove the styles to remove from the page.
//    * Defaults to the global styles
//    */
//   export function uninstallStyles(
//       toRemove: Array<StyleDefinition> =
//       styles.get(Mode.Global) || []): void {
//     if (toRemove) {
//       for (const style of toRemove) {
//         let idx = toRemove.indexOf(style);
//         if (idx != -1) {
//           uninstallStyle(style);
//         } else {
//           throw new Error(`Error: style definition with url ${style.url} is not managed by this theme-manager`);
//         }
//       }
//     }
//   }
//
//
//   /**
//    *
//    * @param definition the definition to add
//    * @private shouldn't be called directly
//    */
//   function enqueueConstructableStyleDefinition(
//       definition: StyleDefinition
//   ): Promise<any> {
//     console.log(`Enqueuing constructable stylesheet with url: ${definition.url}`)
//     const stylesheet = new CSSStyleSheet() as ReplaceAware;
//     if (!definition.url) {
//       return Promise.resolve();
//     }
//     return requestStylesheet(definition.url).then(styleDefinition => {implements StyleInstaller
//       return stylesheet
//           .replace(styleDefinition)
//           .then((success => {
//                 definition.result = stylesheet;
//                 if (definition.mode === Mode.Constructable) {
//                   const doc = document as unknown as {
//                     adoptedStyleSheets: Array<CSSStyleSheet>
//                   }
//                   doc.adoptedStyleSheets = [...doc.adoptedStyleSheets].concat(stylesheet);
//                 }
//                 let event = new CustomEvent(
//                     EventType.CustomStyleAdded,
//                     {
//                       detail: {
//                         eventType: EventType.CustomStyleAdded,
//                         styleDefinition: stylesheet
//                       }
//                     });
//
//                 document
//                     .documentElement
//                     .dispatchEvent(event);
//                 console.info("Successfully loaded stylesheet");
//                 // @ts-ignore
//                 styles.get(definition.mode).push(definition);
//                 return success;
//               }),
//               error => {
//                 console.warn(`Failed to load stylesheet.  Reason: ${error}`);
//                 return Promise.reject(error);
//               });
//     });
//
//   }
//
//   function enqueueGlobalScriptDefinition(definition: ScriptDefinition): Promise<HTMLScriptElement> {
//     console.info(`Enqueing script for addition with if: ${definition.id}, url: ${definition.url}`);
//
//     return new Promise<HTMLScriptElement>(
//         (resolve, reject) => {
//           let script = document.createElement('script');
//           script.type = 'text/javascript';
//           script.id = definition.id as string;
//           script.src = definition.url as string;
//           script.async = definition.async;
//           script.defer = definition.defer;
//           document.head.appendChild(script);
//
//           let event = new CustomEvent(
//               EventType.ScriptAdded, {
//                 detail: {
//                   eventType: EventType.CustomStyleAdded,
//                   scriptDefinition: definition
//                 }
//               });
//           document
//               .documentElement
//               .dispatchEvent(event);
//
//           script.onload = (e: Event) => {
//             console.log(`Successfully added script at ${definition.url}`);
//             resolve(script);
//           }
//           script.onerror = (e: string | Event) => {
//             console.log(`Failed to add style at ${definition.url}.  Reason: ${e}`)
//             reject(script);
//           }
//
//         });
//   }
//
//   /**
//    * @param style the style to install
//    * @private should not be called externally
//    */
//   function enqueueGlobalStyleDefinition(
//       style: StyleDefinition
//   ): Promise<HTMLLinkElement> {
//     console.info(`Enqueuing style for addition with id: ${style.id}, url: ${style.url}`);
//     return new Promise<HTMLLinkElement>((
//         resolve, reject) => {
//       let link = document.createElement('link');
//       link.id = style.id as string;
//       link.href = style.url as string;
//       link.rel = 'stylesheet';
//       style.result = link;
//       document.head.appendChild(link);
//
//       let event = new CustomEvent(
//           EventType.CustomStyleAdded, {
//             detail: {
//               eventType: EventType.CustomStyleAdded,
//               styleDefinition: link
//             }
//           });
//       document
//           .documentElement
//           .dispatchEvent(event);
//
//       link.onload = (e: Event) => {
//         console.info(`Successfully added style at ${style.url}`);
//         resolve(link);
//       }
//       link.onerror = (e: string | Event) => {
//         console.warn(`Failed to add style at ${style.url}.  Reason: ${e}`)
//         reject(link);
//       }
//     });
//   }
//
//   /**
//    *
//    * @param url the URL to load the stylesheet from
//    * @param method the HTTP method to use (defaults to 'GET')
//    */
//   export function requestStylesheet(
//       url: string,
//       method: string = 'GET'
//   ): Promise<string> {
//     console.log(`Attempting to load stylesheet at url ${url} with method ${method}`)
//     return new Promise((resolve, reject) => {
//
//
//       const xhr = new XMLHttpRequest();
//       xhr.open(method, url);
//
//
//       xhr.onload = function () {
//         if (this.status >= 200 && this.status < 300) {
//           console.log(`Successfully loaded stylesheet ${method}`)
//           resolve(xhr.responseText);
//         } else {
//           reject({
//             status: this.status,
//             statusText: xhr.statusText
//           });
//         }
//       };
//
//
//       xhr.onerror = function () {
//         console.log(`Failed to load stylesheet at url ${url} with method ${method}`)
//         reject(new Error(
//             `Encountered an error '${this.statusText}', error: ${this.status}`
//         ));
//       };
//       xhr.send();
//     });
//
//
//   }
// }
//
//
// (window as any).Aire = {
//   ThemeManager: Aire
// };
