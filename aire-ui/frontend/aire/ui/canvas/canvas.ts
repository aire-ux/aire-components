import {css, customElement, html, LitElement, PropertyValues, query, state,} from "lit-element";
import {Graph} from "@antv/x6";
import {Vertex} from "./cell";
import {Receive, Remotable, Remote} from "@aire-ux/aire-condensation";


// @ts-ignore
@customElement('aire-canvas')
@Remotable
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


  @state()
  @query('div.container')
  private container: HTMLDivElement | undefined;

  private createGraph(): void {
    if (this.graph) {
      return;
    }
    const container = this.container;
    if (container) {
      const graph = new Graph({
        panning: true,
        container: container,
        grid: {
          size: 10,
          visible: true
        }
      });
      this.graph = graph;
    }
  }

  @Remote
  public addVertex(@Receive(Vertex) vertex: Vertex) {
    // @ts-ignore
    this.graph?.addNode(vertex as any);
  }

  @Remote
  public addVertices(@Receive(Vertex) vertices: Vertex[]): void {
    // @ts-ignore
    this.graph?.addNodes(vertices);
  }


  @Remote
  registerVertexTemplates(@Receive(VertexTemplate) templates: VertexTemplate[]): void {
    Graph.registerNode(templates);
  }

  protected firstUpdated(_changedProperties: PropertyValues) {
    super.firstUpdated(_changedProperties);
    this.createGraph();
    this.dispatchEvent(new CustomEvent('canvas-ready'))
  }

  render() {
    return html`
      <div class="container">
      </div>
    `
  }
}