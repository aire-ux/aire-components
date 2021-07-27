import {css, customElement, html, LitElement, property,} from 'lit-element';

// @ts-ignore
import {styles} from '../themes/base/aire-button'

@customElement('aire-button')
export class AireButton extends LitElement {


  static get styles() {
    return css`${styles}`;
  }


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
      <button>
        <slot></slot>
      </button>
    `;

  }
}
