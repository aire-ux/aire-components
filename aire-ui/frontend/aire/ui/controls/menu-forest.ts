import {customElement, html, LitElement} from "lit-element";


@customElement('aire-menu-forest')
export class MenuForest extends LitElement {

  protected render(): unknown {
    return html`
      <nav>
        <slot part="header"></slot>
        <slot part="primary"></slot>
        <slot part="footer"></slot>
      </nav>
    `;
  }

}