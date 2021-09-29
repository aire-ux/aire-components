import {
  css,
  customElement,
  html,
  LitElement,
  property,
  PropertyValues
} from 'lit-element';

@customElement('aire-designer')
export class AireDesigner extends LitElement {





  render() {
    return html`
      <div 
          class="aire-designer-container">
      </div>
    `;
  }


  protected firstUpdated(_changedProperties: PropertyValues) {
    super.firstUpdated(_changedProperties);
  }

  protected createRenderRoot(): Element | ShadowRoot {
    return this;
  }
}
