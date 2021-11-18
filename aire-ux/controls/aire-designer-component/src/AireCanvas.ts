import {
  customElement,
  html,
  LitElement,
  property,
  PropertyValues,
} from 'lit-element';

export const HtmlContents = html` <div class="aire-canvas-container"></div> `;

@customElement('aire-canvas')
export class AireCanvas extends LitElement {
  // eslint-disable-next-line no-undef
  private graph: mxGraph;

  @property({
    attribute: 'base-path',
  })
  private basePath: string;

  render() {
    return HtmlContents;
  }

  protected firstUpdated(_changedProperties: PropertyValues) {
    console.log(_changedProperties);
    super.firstUpdated(_changedProperties);
    // eslint-disable-next-line new-cap,no-undef
    this.graph = new mxGraph(this.renderRoot.firstElementChild);
  }

  protected createRenderRoot(): Element | ShadowRoot {
    return this;
  }
}
