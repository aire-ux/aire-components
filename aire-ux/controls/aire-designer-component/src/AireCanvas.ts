import {css, customElement, html, LitElement, property, PropertyValues,} from 'lit-element';
import factory from './mxgraph';
import {Graph} from "./ext/Graph";

/**
 * the html contents of this canvas
 */
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

  /**
   * the base path to use
   * @private
   */
  @property({
    attribute: 'base-path',
  })
  private basePath: string;

  /**
   * the client source location to use.  Useful for debugging
   * @private
   */
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
    /* eslint-disable */
    this._graph = new Graph(this.renderRoot.firstElementChild as Element);
  }


  attributeChangedCallback(
      name: string,
      _old: string | null,
      value: string | null
  ) {
    super.attributeChangedCallback(name, _old, value);

    if (name === 'base-path' && value) {
      // this.mxns = factory({
      //   mxBasePath: value,
      // });
    }
  }
}
