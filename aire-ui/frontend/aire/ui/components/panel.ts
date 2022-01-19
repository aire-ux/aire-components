import {css, customElement, html, LitElement} from "lit-element";

@customElement('aire-panel')
export class AirePanel extends LitElement {

  static styles = css`
    article {
      width:100%;
      height:100%;
    }
  
  `;
  render() {
    return html`
      <article>
        <slot></slot>
      </article>
    `
  }

}