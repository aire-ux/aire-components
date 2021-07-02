/**
 * we can't re-export litelement due to its private members
 */
import {adoptStyles} from 'lit';
import {Aire} from "./AireThemeManager";
import StyleEvent = Aire.StyleEvent;
import EventType = Aire.EventType;

export interface ThemeEventAware {
  /**
   *
   * litelement's connected callback
   */
  connectedCallback(): void;

  /**
   * litelement's disconnected callback
   */
  disconnectedCallback(): void;


  shadowRoot?: ShadowRoot
  /**
   * the adoptedStyleSheets (if they exist)--need polyfills
   */
  adoptedStyleSheets?: ReadonlyArray<CSSStyleSheet>
}

export function dynamicallyThemeable<T extends { new(...args: any[]): {} }>(ctor: T) : any {
  type themeAwareConstructorType = {
    new (...args: any[]) : ({} & ThemeEventAware)
  }
  const themeAwareCtor = ctor as any as themeAwareConstructorType;
  return class extends themeAwareCtor implements ThemeEventAware {
    stylesheetRemovedListener = (e: StyleEvent) => {
      let detail = e.detail,
          definition = detail.styleDefinition,
          stylesheet = definition.result;
      if (
          this.adoptedStyleSheets
          && stylesheet
          && stylesheet instanceof CSSStyleSheet
      ) {
        this.adoptedStyleSheets =
            this
                .adoptedStyleSheets
                .filter(style => style !== stylesheet);
      }
    }

    stylesheetAddedListener = (e: StyleEvent) => {
      let detail = e.detail,
          definition = detail.styleDefinition,
          shadowRoot = this.shadowRoot,
          stylesheet = definition.result;

      if (
          this.adoptedStyleSheets
          && stylesheet
          && shadowRoot
          && stylesheet instanceof CSSStyleSheet
      ) {
        adoptStyles(shadowRoot, [stylesheet]);
      }
    }


    connectedCallback(): void {
      Aire.addStyleChangeListener(
          EventType.CustomStyleAdded,
          this.stylesheetAddedListener,
      )
      Aire.addStyleChangeListener(
          EventType.CustomStyleRemoved,
          this.stylesheetRemovedListener
      );
      super.connectedCallback();
    }


    disconnectedCallback(): void {
      Aire.removeStyleChangeListener(
          EventType.CustomStyleAdded,
          this.stylesheetAddedListener
      );
      Aire.removeStyleChangeListener(
          EventType.CustomStyleRemoved,
          this.stylesheetRemovedListener
      );
      super.connectedCallback();
    }
  }

}