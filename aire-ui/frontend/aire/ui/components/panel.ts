import {css, customElement, html, LitElement} from "lit-element";

@customElement('aire-panel')
export class AirePanel extends LitElement {

  static styles = css`
    article {
      width:100%;
      height:100%;
      position: relative;
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
      <article>
        <slot></slot>
        <slot name="navigation-bar"></slot>
      </article>
    `
  }

}