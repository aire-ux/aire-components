import AireThemeManager, {Registration} from "./AireThemeManager";

export type PageStyleDefinitionProperties = {
  url?: string;
  content?: string;
}


export interface StyleRegistration extends Registration {

}

export class PageStyleDefinition {
  /**
   *
   * @param properties the properties to construct this
   * page style definition with
   */
  constructor(
      readonly properties: PageStyleDefinitionProperties
  ) {

  }

  /**
   * install this page style definition into the page,
   * then register the style registration with the AireThemeManager
   * @param manager the manager to register this style definition
   */
  public install(
      manager: AireThemeManager
  ): Promise<StyleRegistration> {
    const props = this.properties;
    if (props.content) {
      return this.installStyleWithContent(manager);
    } else if (props.url) {
      // return this.installGlobalStyleSheet(manager);
    }
    return Promise.reject(new ErrorStyleRegistration("Unknown style type"))
  }

  /**
   * @param manager the manager to install this into
   * @private
   * @returns the style registration
   */
  private installStyleWithContent(
      manager: AireThemeManager
  ): Promise<StyleRegistration> {
    const
        props = this.properties,
        styleElement = document.createElement('style');
    styleElement.textContent = props.content as string;
    return new Promise((resolve) => {
      document.head.append(styleElement);
      resolve(new LinkStyleRegistration(styleElement));
    });
  }

}

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
  constructor(readonly message: string) {
  }

  remove(): void {

  }
}