import {css, customElement, html, LitElement, property, PropertyValues,} from 'lit-element';
import {Graph} from "./ext/Graph";

export const HtmlContents = html`
  <div class="aire-canvas-container"></div> `;

@customElement('aire-canvas')
export class AireCanvas extends LitElement {
  private _graph:  Graph;


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

  public get graph() : Graph {
    return this._graph;
  }

  protected firstUpdated(_changedProperties: PropertyValues) {
    super.firstUpdated(_changedProperties);
    // @ts-ignore
    this._graph = new mx.mxGraph(this.renderRoot.firstElementChild as Element);
  }

}
