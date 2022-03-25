import {css, customElement, html, HTMLTemplateResult, LitElement} from "lit-element";

@customElement('aire-wizard')
export class Wizard extends LitElement {



  // language=CSS
  static styles = css`
    :host {
      width: 100%;
      height: 100%;
      display: block;
    }
    article {
      display: flex;
      flex-direction: column;
      width: 100%;
      height: 100%;
    }
    
    
  
  `;

  render(): HTMLTemplateResult {
    return html`
      <article>
        <slot name="header" part="header"></slot>
        <slot name="page"></slot>
      </article>
    `;
  }
}