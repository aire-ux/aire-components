import {customElement, html, LitElement, property} from 'lit-element';

// @ts-ignore
import style from "../styles/aire-button.scss"
import {dynamicallyThemeable} from "../node_modules/@aire-ux/aire-theme-manager/dist/src/dynamicallyThemeable.js";

@customElement('aire-button')
@dynamicallyThemeable
export class AireButton extends LitElement {

  @property({
    reflect: true,
    attribute: true
  })
  classes: String;


  @property({
    reflect: true,
    attribute: true
  })
  type: String;


  private dispatchClick(): void {
    this.dispatchEvent(
        new CustomEvent('click', {}));
  }

  render() {
    return html`
      <button
          @click="${this.dispatchClick}"
          class="${this.classes}"
          type="${this.type}"
      >
        <slot></slot>
      </button>
    `;
  }
}
