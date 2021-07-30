import {css, customElement, html, LitElement, property,} from 'lit-element';

// @ts-ignore
import {styles} from '../themes/base/aire-button'

export type Variant = '' | 'small';

@customElement('aire-button')
export class AireButton extends LitElement {


  static get styles() {
    return css`${styles}`;
  }




  @property({
    reflect: true,
    attribute: true
  })
  variants: string;


  render() {
    return html`
      <button class="${this.variants || ''}">
        <slot></slot>
      </button>
    `;

  }
}
