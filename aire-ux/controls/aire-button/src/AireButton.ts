import {customElement, html, LitElement, property} from 'lit-element';

// @ts-ignore
import style from "../styles/aire-button.scss"
import {dynamicallyThemeable} from "@aire-ux/aire-theme-decorators";

@customElement('aire-button')
@dynamicallyThemeable
export class AireButton extends LitElement {

  /**
   * set the style classes for this element
   */
  @property({
    reflect: true,
    attribute: true
  })
  classes: String;


  /**
   * set the type of this button
   */
  @property({
    reflect: true,
    attribute: true
  })
  type: String;


  render() {
    return html`
      <button
          type="${this.type}"
          @click="${this.click}"
          class="${this.classes}"
      >
        <slot></slot>
      </button>
    `;
  }
}
