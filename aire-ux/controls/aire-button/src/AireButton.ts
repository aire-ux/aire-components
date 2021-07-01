import {
  css,
  customElement,
  html,
  LitElement, property
} from 'lit-element';


// @ts-ignore
import style from "../styles/aire-button.scss"

@customElement('aire-button')
export class AireButton extends LitElement {

  @property({
    reflect: true,
    attribute: true
  })
  classes: String;


  @property({
    reflect:true,
    attribute:true
  })
  type: String;


  // static styles = style;


  render() {
    return html`
      <button 
        class="btn btn-primary"
          type="${this.type}"
      >
        <slot></slot>
      </button>
    `;
  }
}
