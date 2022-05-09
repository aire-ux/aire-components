import {customElement, html, LitElement, property} from 'lit-element';


@customElement('aire-form-panel')
export class FormPanel extends LitElement {



  render() {
    return html`
      <section>
        <article>
          <slot name="overview"></slot>
        </article>
        <article>
          <slot></slot>
        </article>
      </section>
    `;
  }
}
