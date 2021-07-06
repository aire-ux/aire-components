import {customElement, html, LitElement, property} from 'lit-element';

// @ts-ignore
import style from "../styles/aire-icon.scss"
import {dynamicallyThemeable} from "@aire-ux/aire-theme-decorators";

@customElement('aire-icon')
@dynamicallyThemeable
export class AireIcon extends LitElement {

  /**
   * set the style classes for this element
   */
  @property({
    reflect: true,
    attribute: true
  })
  classes: String;


  /**
   * set the type of this icon
   */
  @property({
    reflect: true,
    attribute: true
  })
  type: String;


  render() {
    return html`
      <icon
          type="${this.type}"
          @click="${this.click}"
          class="${this.classes}"
      >
        <slot></slot>
      </icon>
    `;
  }
}
