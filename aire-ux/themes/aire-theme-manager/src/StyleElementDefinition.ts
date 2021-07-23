import AireThemeManager, {Registration} from "./AireThemeManager";
import {
  ConstructableStyleSheetAware,
  GlobalPageStyleInstaller,
  InlinePageStyleInstaller,
  RemoteConstructableStyleInstaller
} from "./StyleInstallers";
import {constructStyleSheetFrom} from "./Utilities";
import {Mode, Source, StyleDefinition} from "./Theme";


export interface StyleRegistration extends Registration {

}


export interface StyleInstaller {

  /**
   * the manager
   */
  readonly manager: AireThemeManager;


  install(properties: StyleDefinition): Promise<StyleRegistration>
}

export interface StyleInstallerConstructor {
  new(manager: AireThemeManager): StyleInstaller
}


export class StyleElementDefinition {

  static installers: Map</**
   * the source a stylesheet is coming from
   */
      Source,
      /**
       * map modes to style installers
       */
      Map<Mode,
          /**
           * enforce constructor
           */
          { new(manager: AireThemeManager): StyleInstaller }>>

  static initialize() {
    const bySource = new Map();
    bySource.set('remote', new Map());
    bySource.set('inline', new Map());
    bySource.get('inline').set('page', InlinePageStyleInstaller);
    bySource.get('remote').set('page', GlobalPageStyleInstaller);
    bySource.get('remote').set('constructable', RemoteConstructableStyleInstaller)
    StyleElementDefinition.installers = bySource;
    // bySource.get('remote').set('constructable', InlinePageStyleInstaller)
    // PageStyleDefinition.installers = new Map();
  }

  /**
   *
   * @param properties the properties to construct this
   * page style definition with
   */
  constructor(
      readonly properties: StyleDefinition
  ) {

  }

  public install(
      manager: AireThemeManager
  ): Promise<StyleRegistration> {
    const properties = this.properties,
        installers = StyleElementDefinition.installers,
        sourceMap = installers.get(properties.source),
        installer = sourceMap && sourceMap.get(properties.mode as Mode);
    if (installer) {
      return new installer(manager).install(properties);
    }
    return Promise.reject(
        new ErrorStyleRegistration('no available installer registered')
    );
  }


  /**
   * apply this page style definition to an element. If this is a global
   * style definition.  This takes the content (local or remote) and constructs
   * a constructable stylesheet with it, then applies that stylesheet to this element
   * @param themeManager the manager to use
   * @param element the element to apply this to
   *
   */
  public applyToElement(themeManager: AireThemeManager, element: Element): Promise<CSSStyleSheet | void> {
    if (!element) {
      throw new Error("Error: element must not be null or undefined");
    }
    if (!element.shadowRoot) {
      throw new Error("Error: element must have a shadowroot");
    }

    const shadowRoot = element.shadowRoot as ConstructableStyleSheetAware;
    if (shadowRoot.adoptedStyleSheets) {
      return this.createStyleSheet().then(styleSheet => {
        shadowRoot.adoptedStyleSheets = [
          ...shadowRoot.adoptedStyleSheets as CSSStyleSheet[],
          styleSheet
        ];
      })
    }
    throw new Error("Could not apply stylesheet");
  }


  private createStyleSheet(): Promise<CSSStyleSheet> {
    const properties = this.properties,
        mode = properties.mode;

    if (mode !== 'constructable') {
      throw new Error("Error: cannot construct a stylesheet in 'page' mode");
    }
    return constructStyleSheetFrom(properties);
  }


}

StyleElementDefinition.initialize();


/**
 * any style-related element
 */
export type StyleElement = HTMLStyleElement | HTMLLinkElement;

/**
 * internal registration
 */
export class LinkStyleRegistration implements StyleRegistration {
  constructor(readonly element: StyleElement) {

  }

  remove(): void {
    this.element.remove();
  }
}


export class ErrorStyleRegistration implements StyleRegistration {
  constructor(readonly message: string | Event, readonly element?: StyleElement) {
  }

  remove(): void {
    if (this.element) {
      this.element.remove();
    }
  }
}

