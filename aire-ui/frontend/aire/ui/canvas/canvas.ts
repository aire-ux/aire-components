import {css, customElement, html, LitElement, PropertyValues, query} from "lit-element";
import {Edge, Events, Graph, Node} from "@antv/x6";
import {Dynamic, Receive, Remotable, Remote} from "@aire-ux/aire-condensation";
import {VertexTemplate} from "Frontend/aire/ui/canvas/template";
import {CircularLayout, Edge as LayoutEdge, Model, Node as LayoutNode} from "@antv/layout";
import {
  EdgeDefinition,
  ListenerDefinition,
  ListenerRegistration
} from "Frontend/aire/ui/canvas/cell";

import "./patch";
import EventArgs = Events.EventArgs;


@Remotable
@customElement('aire-canvas')
export class Canvas extends LitElement {

  static layout = new CircularLayout();
  private graph: Graph | undefined;

  @query('div.container')
  private container: HTMLDivElement | undefined;

  private canvasResizeObserver: ResizeObserver | undefined;
  /**
   * gotta track these so we can unregister them
   * @private
   */
  private registeredHandlers: Map<Events.EventNames<EventArgs>, Array<[string, Events.Handler<any>]>>;

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
      max-height: 100%;
      z-index: 100;
    }

    div.container > div.x6-graph-grid {
      position: absolute;
      top: 0;
      bottom: 0;
      left: 0;
      right: 0;
      z-index: -1;
    }
  `;


  constructor() {
    super();
    this.registeredHandlers = new Map<Events.EventNames<EventArgs>, Array<[string, Events.Handler<any>]>>();
  }


  private createGraph(): void {
    if (this.graph) {
      return;
    }
    const container = this.container;
    const graph = new Graph({
      container: container,
      preventDefaultContextMenu: false,
      grid: true
    });
    this.graph = graph;
  }

  disconnectedCallback() {
    super.disconnectedCallback();
    const observer = this.canvasResizeObserver;
    if (observer) {
      observer.disconnect();
    }
  }

  connectedCallback() {
    super.connectedCallback();
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
  public addListener(@Receive(Dynamic) definition: ListenerDefinition): void {
    this.doAddListener(definition);
  }

  @Remote
  public addListeners(@Receive(Dynamic) definitions: Array<ListenerDefinition>): void {
    for (const definition of definitions) {
      this.doAddListener(definition);
    }
  }

  @Remote
  public removeListener(@Receive(Dynamic) definition: ListenerRegistration): boolean {
    const handlers = this.registeredHandlers,
        handlersOfType = handlers.get(definition.key),
        handler = handlersOfType && handlersOfType
            .find(([id, _]) => id === definition.id);

    if (handler) {
      const [_, h] = handler!;
      delete handlersOfType[handlersOfType.indexOf(handler)];
      this.graph!.off(definition.key, h);
    }
    return !!handler;
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
    this.createObservers();
    this.dispatchEvent(new CustomEvent('canvas-ready'));
  }

  private createObservers() {
    const observer = new ResizeObserver((value: Array<ResizeObserverEntry>) => {
      const host = this,
          container = this.container!;
      if (!(Array.isArray(value) && value.length)) {
        // required to prevent ResizeObserver loop limit exceeded
        window.requestAnimationFrame(() => {
          for (const entry of value) {
            container.style.width = `${host.clientWidth}px`;
            container.style.height = `${host.clientHeight}px`;
          }
        });
      }
    });
    observer.observe(this);
    this.canvasResizeObserver = observer;
  }

  private doAddListener(definition: ListenerDefinition): void {

    const handler = (o: any) => {
          const {e, cell} = o;
          this.dispatchEvent(new CustomEvent(definition.category, {

            detail: {
              source: {
                id: cell.id,
                targetEventType: definition.targetEventType,
                location: {
                  x: e?.originalEvent.clientX,
                  y: e?.originalEvent.clientY
                }
              }
            }
          }))
        },
        handlers = this.registeredHandlers,
        hlist = handlers.get(definition.key) || [];
    if (!handlers.has(definition.key)) {
      handlers.set(definition.key, hlist)
    }
    hlist.push([definition.id, handler]);
    this.graph!.on(definition.key, handler);
  }


}