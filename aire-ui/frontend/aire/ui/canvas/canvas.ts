import {customElement, html, LitElement} from "lit-element";
import {Graph} from "@antv/x6";

@customElement('aire-canvas')
export class Canvas extends LitElement {

  private graph: Graph | undefined;


  connectedCallback() {
    super.connectedCallback();
    const graph = new Graph({
      container: this
    });

    this.graph = graph;
    this.graph.addNode({
      x: 20,
      y: 20,
      width: 30,
      height: 30
    });
  }

  disconnectedCallback() {
    super.disconnectedCallback();
    console.log("disconnected");
    const graph = this.graph;
    if (graph) {
      graph.dispose();
    }
  }

  render() {
    return html`
      <div class="container" style="height: 300px"></div>
    `;
  }

}