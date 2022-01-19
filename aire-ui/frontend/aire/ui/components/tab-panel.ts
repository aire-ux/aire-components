import {customElement, html, LitElement} from "lit-element";

@customElement('aire-tab-panel')
export class TabPanel extends LitElement {

  render() {
    return html`
      <article>
        <slot name="tabs"></slot>
        <slot name="content"></slot>
      </article>
    `;
  }

}