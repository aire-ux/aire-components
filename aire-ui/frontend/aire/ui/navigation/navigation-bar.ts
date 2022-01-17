import {customElement, html, LitElement} from "lit-element";

@customElement('aire-navigation-bar')
export class NavigationBar extends LitElement {

  protected render(): unknown {
    return html`
      <nav>
        <slot>
          hello
        </slot>
      </nav>
    `
  }
}