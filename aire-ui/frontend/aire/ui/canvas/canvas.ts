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
    this.dispatchEvent(new CustomEvent('canvas-ready'));
    Graph.registerNode(
        'custom-polygon',
        {
          inherit: 'polygon',
          width: 200,
          height: 200,
          attrs: {
            body: {
              strokeWidth: 1,
              stroke: '#660066',
              fill: 'rgba(163, 102, 163, 0.32)',
              // refPoints: '-25,0 -12.5,12.5 12.5,12.5 25,0 12.5,-12.5 -12.5,-12.5'
              // refPoints: '0.000000,25.000000 19.545787,15.587245 24.373198,-5.563023 10.847093,-22.524222 -10.847093,-22.524222 -24.373198,-5.563023 -19.545787,15.587245'
              //refPoints: '0.000000,25.000000 11.618079,22.136401 20.574597,14.201619 24.817722,3.013417 23.375406,-8.865122 16.578066,-18.712769 5.982892,-24.273545 -5.982892,-24.273545 -16.578066,-18.712769 -23.375406,-8.865122 -24.817722,3.013417 -20.574597,14.201619 -11.618079,22.136401'
              refPoints: '0.000000,25.000000 13.742941,-20.883763 -22.960346,9.890526 24.616934,4.359652 -18.167192,-17.174200 5.735012,24.333303 8.585700,-23.479475 -20.079151,14.893881 24.960559,-1.403748 -21.622481,-12.548638 11.164143,22.368771 2.970535,-24.822891 -16.127019,19.102860'
            }
          },
        },
        true,
    );

    const graph = this.graph;
    setTimeout(() => {
      // @ts-ignore
      graph?.addNode({
        x: 50,
        y: 50,
        width: 50,
        height: 50,
        shape: 'custom-polygon'
      });
    }, 200)

  }

  render() {
    return html`
      <div class="container">
      </div>
    `
  }
}