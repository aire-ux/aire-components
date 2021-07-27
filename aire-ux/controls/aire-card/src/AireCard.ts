import {css, customElement, html, LitElement, property,} from 'lit-element';

// @ts-ignore
import {styles} from '../themes/base/aire-card'

@customElement('aire-card')
export class AireCard extends LitElement {


  static get styles() {
    return css`${styles}`;
  }




  render() {
    return html`
      <div>
        aire-card
      </div>
    `;

  }
}
