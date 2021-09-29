import {
  css,
  customElement,
  html,
  LitElement,
  property,
  PropertyValues
} from 'lit-element';
import {Designer} from "./designer/core/designer";

@customElement('aire-designer')
export class AireDesigner extends LitElement {

  private designer: Designer;



  render() {
    return html`
      <div 
          class="aire-designer-container">
      </div>
    `;
  }


  protected firstUpdated(_changedProperties: PropertyValues) {
    super.firstUpdated(_changedProperties);
    new Designer(this.renderRoot.firstElementChild as Element);

  }

  protected createRenderRoot(): Element | ShadowRoot {
    return this;
  }
}
