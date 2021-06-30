import {
  css,
  customElement,
  html,
  LitElement
} from 'lit-element';


// @ts-ignore
import style from "../styles/aire-button.scss"

@customElement('aire-button')
export class AireButton extends LitElement {

  static styles = style;


  render() {
    return html`
      <button type="button" class="btn btn-outline-primary">
        <slot></slot>
      </button>
    `;
  }
}
