import {css, customElement, html, LitElement} from "lit-element";

@customElement('aire-navigation-bar-button')
export class NavigationBarButton extends LitElement {

  static styles = css`
    :host(.container-end) {
      position: absolute;
    }
    :host {
      align-items: center;
      justify-content: center;
    }
    
    ::slotted(img) {
      width: 32px;
      height: 32px;
    }
    ::slotted(vaadin-icon) {
      width:16px;
      height:16px;
    }
  `;


  render() {
    return html`
      <slot part="content"></slot>
    `;
  }

}