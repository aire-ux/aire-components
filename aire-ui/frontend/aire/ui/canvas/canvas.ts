import {css, customElement, LitElement, PropertyValues,} from "lit-element";
import {Graph, Node} from "@antv/x6";
import {Dynamic, Receive, Remotable, Remote} from "@aire-ux/aire-condensation";
import {VertexTemplate} from "Frontend/aire/ui/canvas/template";


@customElement('aire-canvas')
@Remotable
export class Canvas extends LitElement {

  private graph: Graph | undefined;

  // language=CSS
  static styles = css`

  `;


  protected createRenderRoot(): Element | ShadowRoot {
    this.style.width = '100%';
    this.style.height = '100%';
    this.style.border = '1px solid red';
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
    this.graph.addNode({x: 200, y: 200, width: 200, height: 200});
  }

  @Remote
  public addVertex(@Receive(Dynamic) vertex: Node.Metadata): void {
    this.graph!.addNode(vertex);
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
  public addVertexTemplate(@Receive(Dynamic) template: VertexTemplate): Node.Definition {
    return Graph.registerNode(template.name, template as any, true);
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