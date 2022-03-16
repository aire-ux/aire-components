import {customElement, html, HTMLTemplateResult, LitElement,} from "lit-element";

@customElement('aire-wizard')
export class Wizard extends LitElement {



  render(): HTMLTemplateResult {
    return html`
      <article>
        <slot name="progress"></slot>
        <slot name="page"></slot>
      </article>
    `;
  }
}