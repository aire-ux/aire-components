import { customElement, html, LitElement, PropertyValues } from 'lit-element';

@customElement('aire-designer')
export class AireDesigner extends LitElement {
  // eslint-disable-next-line no-undef
  private graph: mxGraph;

  render() {
    return html` <div class="aire-designer-container"></div> `;
  }

  protected firstUpdated(_changedProperties: PropertyValues) {
    super.firstUpdated(_changedProperties);
    // eslint-disable-next-line new-cap,no-undef
    this.graph = new mxGraph(this.renderRoot.firstElementChild);
  }

  protected createRenderRoot(): Element | ShadowRoot {
    return this;
  }
}
