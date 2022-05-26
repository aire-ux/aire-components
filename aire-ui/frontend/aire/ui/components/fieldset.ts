import {customElement, html, LitElement, property} from 'lit-element';


@customElement('aire-fieldset')
export class Fieldset extends LitElement {

  @property({
    attribute: 'legend'
  })
  legend: string | undefined;


  render() {
    return html`

      <fieldset>
        <legend>${this.legend}</legend>
        <slot></slot>
      </fieldset>
    `;
  }
}