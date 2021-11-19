import { css, html, customElement, LitElement } from 'lit-element';

@customElement('aire-designer')
export class AireDesigner extends LitElement {
  static get styles() {
    return css`
      aire-designer {
        display: block;
      }
    `;
  }

  protected render() {
    return html`<slot></slot>`;
  }
}
