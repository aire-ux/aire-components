import {css, customElement, LitElement, PropertyValues,} from "lit-element";
import {Graph, Node} from "@antv/x6";
import {Dynamic, Receive, Remotable, Remote} from "@aire-ux/aire-condensation";
import {VertexTemplate} from "Frontend/aire/ui/canvas/template";
import {CircularLayout, DagreLayout} from "@antv/layout";


@Remotable
@customElement('aire-canvas')
export class Canvas extends LitElement {

  private graph: Graph | undefined;

  // language=CSS
  static styles = css`
    :host {
      width: 100%;
      height: 100%;
      display: block;
    }
  `;


  protected createRenderRoot(): Element | ShadowRoot {
    this.style.width = '100%';
    this.style.height = '100%';
    return this;
  }

  private createGraph(): void {
    if (this.graph) {
      return;
    }
    const graph = new Graph({
      container: this,
      preventDefaultContextMenu: false,
      grid: true
    });
    this.graph = graph;
    this.registerListeners(graph);
  }

  @Remote
  public addVertex(@Receive(Dynamic) vertex: Node.Metadata): Node.Metadata {
    this.graph!.addNode(vertex);
    return vertex;
  }

  @Remote
  public addVertices(@Receive(Dynamic) vertices: Array<Node.Metadata>): void {
    this.graph!.addNodes(vertices);
  }

  @Remote
  public setVertices(@Receive(Dynamic) vertices: Array<Node.Metadata>): void {
    this.graph!.addNodes(vertices);
  }

  @Remote
  public addVertexTemplate(@Receive(Dynamic) template: VertexTemplate): VertexTemplate {
    Graph.registerNode(template.name, template as any, true);
    return template;
  }


  @Remote
  public removeVertexTemplate(@Receive(Dynamic) template: VertexTemplate): void {
    Graph.unregisterNode(template.name);
  }


  protected firstUpdated(_changedProperties: PropertyValues) {
    super.firstUpdated(_changedProperties);
    this.createGraph();
    this.dispatchEvent(new CustomEvent('canvas-ready'));
  }

  private registerListeners(graph: Graph) {
    graph.on('blank:click', (event) => {
      this.dispatchEvent(new CustomEvent('canvas-clicked', {
        detail: {
          click: {
            x: event.x,
            y: event.y
          }
        }
      }));
    });

  }
}