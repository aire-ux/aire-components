import {css, customElement, html, LitElement} from "lit-element";

@customElement('aire-tab-panel')
export class TabPanel extends LitElement {

  static styles = css`
   
   ::slotted(section) {
     display: flex;
     flex-direction: column;
     position: absolute;
     top:50px;
     bottom:0px;
     left:0px;
     right:0px;
   } 
  `

  render() {
    return html`
      <article>
        <slot name="tabs"></slot>
        <slot name="content"></slot>
      </article>
    `;
  }

}