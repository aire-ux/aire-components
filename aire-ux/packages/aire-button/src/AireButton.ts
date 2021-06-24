import {
  css,
  customElement,
  html,
  LitElement
} from 'lit-element';


// @ts-ignore
import style from "./aire-button.scss"

@customElement('aire-button')
export class AireButton extends LitElement {

  static styles = style;


  render() {
    return html`
      <h1 class="aire-button">Hello</h1>
      <h2>Sup</h2>
    `;
  }
}
