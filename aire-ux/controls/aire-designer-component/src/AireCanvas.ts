import {
  css,
  customElement,
  html,
  LitElement,
  property,
  PropertyValues,
} from 'lit-element';
import factory from './mxgraph';

/**
 * the html contents of this canvas
 */
export const HtmlContents = html` <div class="aire-canvas-container"></div> `;

@customElement('aire-canvas')
export class AireCanvas extends LitElement {
  private mx: any;

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

  protected firstUpdated(_changedProperties: PropertyValues) {
    super.firstUpdated(_changedProperties);
    const { mx } = this;
    /* eslint-disable */
    const graph = new mx.mxGraph(this.renderRoot.firstElementChild);

    // Enables rubberband selection
    new mx.mxRubberband(graph);

    // Gets the default parent for inserting new cells. This
    // is normally the first child of the root (ie. layer 0).
    const parent = graph.getDefaultParent();

    // Adds cells to the model in a single step
    graph.getModel().beginUpdate();
    try {
      const v1 = graph.insertVertex(parent, null, 'Hello,', 20, 20, 80, 30);
      const v2 = graph.insertVertex(parent, null, 'World!', 200, 150, 80, 30);
      graph.insertEdge(parent, null, '', v1, v2);
    } finally {
      // Updates the display
      graph.getModel().endUpdate();
    }
  }

  attributeChangedCallback(
    name: string,
    _old: string | null,
    value: string | null
  ) {
    super.attributeChangedCallback(name, _old, value);

    if (name === 'base-path' && value) {
      this.mx = factory({
        mxBasePath: value,
      });
    }
  }
}
