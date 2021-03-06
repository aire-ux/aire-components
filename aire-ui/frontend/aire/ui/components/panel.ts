import {css, customElement, html, LitElement} from "lit-element";

@customElement('aire-panel')
export class AirePanel extends LitElement {

  static styles = css`
    
    
    ::slotted(section) {
      max-width: calc(100% - 24px);
      border: 1px solid blue;
    }
    ::slotted(aire-navigation-bar) {
      position: absolute !important;
      right:0 !important;
      width: 24px !important;
      top: 0 !important;
      background-color: #f2f2f2;
    }
  
  `;

  render() {
    return html`
      <slot part="content"></slot>
      <slot name="navigation-bar"></slot>
    `
  }

}