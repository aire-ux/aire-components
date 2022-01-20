import {css, customElement, html, LitElement, property} from "lit-element";

@customElement('aire-navigation-bar-button')
export class NavigationBarButton extends LitElement {


  @property()
  rotate: boolean | undefined;

  static styles = css`
    :host(.container-end) {
      position: absolute;
    }
    :host {
      align-items: center;
      justify-content: center;
    }
    
    :not(.aire-drawer-button)::slotted(img) {
      width: 32px;
      height: 32px;
    }
    :not(.aire-drawer-button)::slotted(vaadin-icon) {
      width:16px;
      height:16px;
    }
    
  `;


  get computeRotatedHeight(): number {
    let sibling = this.previousElementSibling,
        result = 0;
    while(sibling) {
      result += sibling.clientWidth - 20;
      sibling = sibling.previousElementSibling;
    }
    return result - 5;
  }

  render() {

    return html`
      <style>
        :host([rotate]) {
          top: ${this.computeRotatedHeight}px !important;
        }
      </style>
      <slot part="content"></slot>
    `;
  }

}