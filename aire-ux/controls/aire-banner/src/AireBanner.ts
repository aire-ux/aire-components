import {css, customElement, html, LitElement, property,} from 'lit-element';

// @ts-ignore
import {styles} from '../themes/base/aire-button'

@customElement('aire-banner')
export class AireBanner extends LitElement {


  static get styles() {
    return css`${styles}`;
  }


}
