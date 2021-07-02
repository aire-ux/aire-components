/**
 * we can't re-export litelement due to its private members
 */
import {adoptStyles} from 'lit';
import {Aire} from '@aire-ux/aire-theme-manager';
// import {Aire} from "@aire";
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
          stylesheet = detail.styleDefinition,
          shadowRoot = this.shadowRoot as any;

      if (
          shadowRoot.adoptedStyleSheets
          && stylesheet
          && shadowRoot
          && stylesheet instanceof CSSStyleSheet
      ) {
        console.log("Successfully adopted styles!");
        adoptStyles(shadowRoot, [stylesheet]);
      }
    }


    connectedCallback(): void {
      console.log("Connected");
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
      console.log("Disconnected");
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