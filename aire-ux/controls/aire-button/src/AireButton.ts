import {
  css,
  customElement,
  html,
  LitElement, property, PropertyValues
} from 'lit-element';

import {adoptStyles} from 'lit';

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


  updateStyles() : void {
    adoptStyles(this.shadowRoot as ShadowRoot, [(window as any).style])
  }

  render() {
    return html`
      <button 
          @click="${this.updateStyles}"
        class="btn btn-primary"
          type="${this.type}"
      >
        <slot></slot>
      </button>
    `;
  }
}
