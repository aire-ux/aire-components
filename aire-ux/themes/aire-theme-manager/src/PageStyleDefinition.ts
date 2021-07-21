import AireThemeManager, {Registration} from "./AireThemeManager";

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
  /**
   * url or actual textual CSS, depending on the source
   */
  content: string;

  /**
   * the source (remote or inline)
   */
  source: Source;

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



/**
 * installs an inline page style,
 * e.g.
 * <style type="text/css">
 *   content
 * </style>
 *
 */
class InlinePageStyleInstaller implements StyleInstaller {

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


/**
 * installs a global page style to the document's head, e.g.
 *
 * <link rel="stylesheet" type="text/css" href="properties.conent"></link>
 */
class GlobalPageStyleInstaller implements StyleInstaller {
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

  // /**
  //  * install this page style definition into the page,
  //  * then register the style registration with the AireThemeManager
  //  * @param manager the manager to register this style definition
  //  */
  // public install(
  //     manager: AireThemeManager
  // ): Promise<StyleRegistration> {
  //   const props = this.properties;
  //   if (props.source === 'remote') {
  //
  //   } else if (props.content)
  //       // if (props.content) {
  //       //   return this.installStyleWithContent(manager);
  //       // } else if (props.url) {
  //       //   return this.installGlobalStyleSheet(manager);
  //       // }
  //     return Promise.reject(new ErrorStyleRegistration("Unknown style type"))
  // }
  //
  // /**
  //  * @param manager the manager to install this into
  //  * @private
  //  * @returns the style registration
  //  */
  // private installStyleWithContent(
  //     manager: AireThemeManager
  // ): Promise<StyleRegistration> {
  // }
  //
  // }
}

PageStyleDefinition.initialize();


/**
 * any style-related element
 */
export type StyleElement = HTMLStyleElement | HTMLLinkElement;

/**
 * internal registration
 */
class LinkStyleRegistration implements StyleRegistration {
  constructor(readonly element: StyleElement) {

  }

  remove(): void {
    this.element.remove();
  }
}


class ErrorStyleRegistration implements StyleRegistration {
  constructor(readonly message: string | Event, readonly element?: StyleElement) {
  }

  remove(): void {
    if (this.element) {
      this.element.remove();
    }
  }
}

