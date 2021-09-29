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


  private graph: mxGraph;



  render() {
    return html`
      <div 
          class="aire-designer-container">
      </div>
    `;
  }


  protected firstUpdated(_changedProperties: PropertyValues) {
    super.firstUpdated(_changedProperties);
    this.graph = new mxGraph(this.renderRoot.firstElementChild);
  }

  protected createRenderRoot(): Element | ShadowRoot {
    return this;
  }
}
