import {css, customElement, html, LitElement} from "lit-element";

@customElement('aire-tab-panel')
export class TabPanel extends LitElement {

  static styles = css`
    
    
    ::slotted(section) {
      position: absolute;
      width: 100%;
      height: calc(100% - 50px);
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
      <slot name="tabs"></slot>
      <article>
        <slot name="content"></slot>
      </article>
    `;
  }

}