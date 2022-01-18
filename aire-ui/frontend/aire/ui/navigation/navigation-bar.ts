import {css, customElement, html, LitElement} from "lit-element";

@customElement('aire-navigation-bar')
export class NavigationBar extends LitElement {

  static styles = css`
    nav {
    
    }
  `;

  protected render(): unknown {
    return html`
      <nav>
        <slot>
        </slot>
      </nav>
    `
  }
}