import {html, css, customElement, LitElement} from "lit-element";

@customElement('aire-property-panel')
export class PropertyPanel extends LitElement {

  static styles = css`
    ::slotted(header) {
      height: 36px;
      display: flex;
      flex-direction: row;
      padding: 4px;
      align-items: center;
    }
    ::slotted(header) > h1 {
      color: white;
    }
    
    ::slotted(section) {
      width: 100%;
      display: flex;
    }
  `

  render() {
    return html`
      <slot name="header" part="header"></slot>
      <slot name="content" part="content"></slot>
    `;
  }

}