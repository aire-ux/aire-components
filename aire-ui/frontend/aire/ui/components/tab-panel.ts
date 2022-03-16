import {css, customElement, html, LitElement} from "lit-element";

@customElement('aire-tab-panel')
export class TabPanel extends LitElement {

  static styles = css`
    :host {
      width: 100%;
    }
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

  private computeSiblingWidth() : number {
    const nextSibling = this.nextElementSibling;
    if(nextSibling && nextSibling.tagName === 'AIRE-NAVIGATION-BAR') {
      return nextSibling.clientWidth + 6;
    }
    return 0;
  }

  render() {
    return html`
      <style>
        ::slotted(section), ::slotted(nav) {
          max-width: calc(100% - ${this.computeSiblingWidth()}px);
        }
      </style>
      <article>
        <slot name="tabs"></slot>
        <slot name="content"></slot>
      </article>
    `;
  }

}