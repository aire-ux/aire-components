import {css, customElement, html, LitElement, PropertyValues, query, state,} from "lit-element";
import {Graph, Node} from "@antv/x6";
import {Dynamic, Receive, Remotable, Remote} from "@aire-ux/aire-condensation";
import {VertexTemplate} from "Frontend/aire/ui/canvas/template";


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
    this.dispatchEvent(new CustomEvent('canvas-ready'))
  }

  render() {
    return html`
      <div class="container">
      </div>
    `
  }
}