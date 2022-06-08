import {css, customElement, html, LitElement} from "lit-element";

@customElement('aire-tab-panel')
export class TabPanel extends LitElement {

  static styles = css`
    article {
      width: 100%;
    }
    
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
      <article>
        <slot name="tabs"></slot>
        <slot name="content"></slot>
      </article>
    `;
  }

}