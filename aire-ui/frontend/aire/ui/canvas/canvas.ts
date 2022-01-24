import {css, customElement, html, LitElement, PropertyValues, query} from "lit-element";
import {Graph} from "@antv/x6";

@customElement('aire-canvas')
export class Canvas extends LitElement {

  private graph: Graph | undefined;

  static styles = css`
    :host {
      width: 100%;
      height: 100%;
      flex: 1;
      display: flex;
    }
    div.container {
      flex: 1;
    }
  `;


  @query('div.container')
  private container: HTMLDivElement | undefined;

  protected firstUpdated(_changedProperties: PropertyValues) {
    super.firstUpdated(_changedProperties);
    this.createGraph();
  }

  connectedCallback() {
    super.connectedCallback();
    this.createGraph();
  }

  private createGraph() : void {
    if(this.graph) {
      return;
    }
    const container = this.container;
    if(container) {
      const graph = new Graph({
        panning: true,
        container: container,
        grid : {
          size: 10,
          visible: true
        }
      });


      this.graph = graph;
      this.graph.addNode({
        x: 100,
        y: 100,
        width: 30,
        height: 30,
        label: 'hello'
      });
      graph.centerContent();
    }
  }

  disconnectedCallback() {
    super.disconnectedCallback();
    const graph = this.graph;
    if (graph) {
      graph.dispose();
    }
  }

  render() {
    return html`
      <div class="container">

      </div>
    `
  }
}