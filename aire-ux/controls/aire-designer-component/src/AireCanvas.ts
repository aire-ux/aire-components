import * as mx from '@aire-ux/mxgraph';
import {
  css,
  customElement,
  html,
  LitElement,
  property,
  PropertyValues,
} from 'lit-element';

export const HtmlContents = html` <div class="aire-canvas-container"></div> `;

@customElement('aire-canvas')
export class AireCanvas extends LitElement {
  private _graph: mx.mxGraph;

  static get styles() {
    return css`
      aire-canvas {
        display: block;
      }
    `;
  }

  @property({
    attribute: 'base-path',
  })
  private basePath: string;

  @property({
    attribute: 'client-source',
  })
  private clientSource: string;

  render() {
    return HtmlContents;
  }

  public get graph(): mx.mxGraph {
    return this._graph;
  }

  protected firstUpdated(_changedProperties: PropertyValues) {
    super.firstUpdated(_changedProperties);
    this._graph = new mx.mxGraph(
      this.renderRoot.firstElementChild as HTMLElement
    );
  }
}
