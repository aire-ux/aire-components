import {css, customElement, html, LitElement, PropertyValues} from "lit-element";

@customElement('aire-navigation-bar')
export class NavigationBar extends LitElement {

  static styles = css`
    nav {
      width: 100%;
      height:100%;
      display: flex;
    }
    
    :host(.horizontal) nav {
      align-items: center;
      flex-direction: row-reverse;
      padding-right: 8px;
    }
    
    :host(.vertical) nav {
      flex-direction:column;
    }
    
  `;

  constructor() {
    super();
  }

  protected firstUpdated(_changedProperties: PropertyValues) {
    super.firstUpdated(_changedProperties);
  }

  protected render(): unknown {
    return html`
      <style>
        :host.verticalright {
          right: ${this.parentElement?.clientWidth}px;
        }
      </style>
      <nav part="navigation">
        <slot>
        </slot>
        <slot name="drawer" part="drawer"></slot>
      </nav>
    `
  }
}