import {css, customElement, html, LitElement, PropertyValues, query} from "lit-element";
import {Edge, Graph, Node} from "@antv/x6";
import {Dynamic, Receive, Remotable, Remote} from "@aire-ux/aire-condensation";
import {VertexTemplate} from "Frontend/aire/ui/canvas/template";
import {CircularLayout, Edge as LayoutEdge, Model, Node as LayoutNode} from "@antv/layout";
import {EdgeDefinition} from "Frontend/aire/ui/canvas/cell";


@Remotable
@customElement('aire-canvas')
export class Canvas extends LitElement {

  static layout = new CircularLayout();
  private graph: Graph | undefined;

  @query('div.container')
  private container: HTMLDivElement | undefined;
  // language=CSS
  static styles = css`
    :host {
      width: 100%;
      height: 100%;
      display: flex;
      align-items: center;
      justify-items: center;
      position: relative;
    }

    div.container {
      flex: 1 1 auto;
      height: 100%;
      max-width: 100%;
      max-height:100%;
    }
    div.container > div.x6-graph-grid {
      position: absolute;
      top: 0;
      bottom:0;
      left:0;
      right:0;
      z-index: -1;
    }
  `;

  constructor() {
    super();
  }

  private createGraph(): void {
    if (this.graph) {
      return;
    }
    console.log(this);
    console.log(this.shadowRoot!.querySelector('div.container'));
    const container = this.container;
    console.log(this.container);
    const graph = new Graph({
      container: container,
      autoResize: container,
      preventDefaultContextMenu: false,
      grid: true
    });


    this.graph = graph;
    this.registerListeners(graph);
  }

  @Remote
  public addVertex(@Receive(Dynamic) vertex: Node.Metadata): Node.Metadata {
    const graph = this.graph,
        previousNodes = graph?.getNodes(),
        nodes = Canvas.layout.layout({
          nodes: [graph?.createNode(vertex)].concat(previousNodes ?? []),
          edges: graph?.getEdges()
        } as Model);
    this.graph = this.graph!.fromJSON(Canvas.layout.layout(nodes as any));
    this.graph.centerContent();
    return vertex;
  }

  @Remote
  public addVertices(@Receive(Dynamic) vertices: Array<Node.Metadata>): void {
    const graph = this.graph!,
        existingNodes = (graph.getNodes().concat(...vertices as any[])) as unknown as LayoutNode[],
        existingEdges = graph.getEdges() as unknown as LayoutEdge[],
        model = {
          nodes: existingNodes,
          edges: existingEdges
        } as Model,
        layout = Canvas.layout;
    if (existingNodes.length == 1) {
      graph.fromJSON(model);
      graph.centerContent();
    } else {
      const configuredModel = layout.layout(model);
      graph.fromJSON(configuredModel);
      graph.centerContent();
    }
  }

  @Remote
  public connectVertices(@Receive(Dynamic) edges: Array<EdgeDefinition>): void {
    const edgeMetadata: Array<Edge.Metadata> = edges.map(edge => {
      return {
        source: edge.source,
        target: edge.target,
        attrs: edge.template.attrs,
        connector: edge.template.connector
      }
    });
    this.graph?.addEdges(edgeMetadata);
  }

  @Remote
  public setVertices(@Receive(Dynamic) vertices: Array<Node.Metadata>): void {

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


  protected render(): unknown {
    return html`
      <div class="container">
      </div>
    `;
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