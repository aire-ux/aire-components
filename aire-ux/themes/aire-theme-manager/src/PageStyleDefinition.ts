import AireThemeManager, {Registration} from "./AireThemeManager";
import {
  GlobalPageStyleInstaller,
  InlinePageStyleInstaller,
  RemoteConstructableStyleInstaller
} from "./StyleInstallers";

/**
 * 1. Remote content must be loaded from the provided URL
 * 2. Inline content may be used
 */
export type Source = 'remote' | 'inline';


export type Mode = 'page' | 'constructable';

/**
 * definition for a page style definition
 *
 * 1. inline & constructable -> CSSStyleSheet must be constructed with the text rather than the URL
 * 2. global & constructable ->
 */
export type PageStyleDefinitionProperties = {

  mode: Mode;

  forceAdopt?: boolean
  /**
   * url or actual textual CSS, depending on the source
   */
  content: string;

  /**
   * the source (remote or inline)
   */
  source: Source;

  /**
   *
   * an optional function used for requesting remote data
   * @param url the url to fetch
   * @param method the HTTP method to use
   */
  urlLoader ?: (url: string, method: string) => Promise<string>

}


export interface StyleRegistration extends Registration {

}

export interface StyleInstaller {

  /**
   * the manager
   */
  readonly manager: AireThemeManager;


  install(properties: PageStyleDefinitionProperties): Promise<StyleRegistration>
}

export interface StyleInstallerConstructor {
  new(manager: AireThemeManager): StyleInstaller
}



export class PageStyleDefinition {

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
    PageStyleDefinition.installers = bySource;
    // bySource.get('remote').set('constructable', InlinePageStyleInstaller)
    // PageStyleDefinition.installers = new Map();
  }

  /**
   *
   * @param properties the properties to construct this
   * page style definition with
   */
  constructor(
      readonly properties: PageStyleDefinitionProperties
  ) {

  }

  public install(
      manager: AireThemeManager
  ): Promise<StyleRegistration> {
    const properties = this.properties,
        installers = PageStyleDefinition.installers,
        sourceMap = installers.get(properties.source),
        installer = sourceMap && sourceMap.get(properties.mode);
    if (installer) {
      return new installer(manager).install(properties);
    }
    return Promise.reject(
        new ErrorStyleRegistration('no available installer registered')
    );
  }
}

PageStyleDefinition.initialize();


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

