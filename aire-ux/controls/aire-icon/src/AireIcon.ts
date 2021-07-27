import {css, customElement, html, LitElement, property,} from 'lit-element';

// @ts-ignore
import {styles} from '../themes/base/aire-icon'

@customElement('aire-icon')
export class AireIcon extends LitElement {


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
      <div>aire-icon</div>
    `;

  }
}
