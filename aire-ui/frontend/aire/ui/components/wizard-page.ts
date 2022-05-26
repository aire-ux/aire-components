import {css, customElement, html, HTMLTemplateResult, LitElement} from "lit-element";

@customElement('aire-wizard-page')
export class WizardPage extends LitElement {



  // language=CSS
  static styles = css`
    section {
      width: 100%;
      display: flex;
      flex-direction: column;
    }
    ::slotted(footer) {
      align-self: flex-end;
    }
  `;
  render(): HTMLTemplateResult {
    return html`
      <section>
        <slot name="header"></slot>
        <slot name="content" part="content"></slot>
        <slot name="footer"></slot>
      </section>
    `;
  }
}
