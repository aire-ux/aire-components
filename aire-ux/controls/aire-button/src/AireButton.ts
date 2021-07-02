import {
  css,
  customElement,
  html,
  LitElement, property, PropertyValues
} from 'lit-element';

import {adoptStyles} from 'lit';

// @ts-ignore
import style from "../styles/aire-button.scss"
import {
  dynamicallyThemeable
} from "../node_modules/@aire-ux/aire-theme-manager/dist/src/dynamicallyThemeable.js";

@customElement('aire-button')
@dynamicallyThemeable
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
