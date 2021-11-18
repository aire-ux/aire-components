import { customElement, html, LitElement, property } from 'lit-element';

export const HtmlContents = html` <div class="aire-canvas-container"></div> `;

const scriptId = 'aire-graph-base-path';

const eventName = 'mx-graph-update-complete';

const listener =
  (tag: Element) =>
  // eslint-disable-next-line @typescript-eslint/no-unused-vars
  (e: Event) => {
    const event = new CustomEvent(eventName);
    document.dispatchEvent(event);
    tag.removeEventListener(eventName as any, this as any);
  };

const installClientSource = (clientSource: string) => {
  const id = `${scriptId}-client-source`;
  let tag: HTMLScriptElement = document.querySelector(
    `script#${id}`
  ) as HTMLScriptElement;
  if (!tag) {
    tag = document.createElement('script');
    tag.type = 'text/javascript';

    tag.addEventListener('load', listener(tag));
    tag.src = clientSource;
    tag.id = id;
    tag.async = false;
    tag.defer = false;
    document.head.appendChild(tag);
  }
};
const installBasePath = (basePath: string) => {
  const id = `${scriptId}-base-path`;
  let tag: HTMLScriptElement = document.querySelector(
    `script#${id}`
  ) as HTMLScriptElement;
  if (!tag) {
    tag = document.createElement('script') as HTMLScriptElement;
    tag.type = 'text/javascript';
    tag.appendChild(
      document.createTextNode(`
      mxBasePath = "${basePath}";
    `)
    );
    tag.id = id;
    document.head.appendChild(tag);
  }
};

@customElement('aire-canvas')
export class AireCanvas extends LitElement {
  // eslint-disable-next-line no-undef
  private graph: mxGraph;

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

  // eslint-disable-next-line no-undef
  private listener: EventListener;

  constructor() {
    super();
    this.listener = this.initialize.bind(this);
    document.addEventListener(eventName, this.listener);
  }

  // eslint-disable-next-line @typescript-eslint/no-unused-vars
  protected async initialize(e: Event): Promise<void> {
    // eslint-disable-next-line new-cap,no-undef
    this.graph = new mxGraph(this.renderRoot.firstElementChild);
    document.removeEventListener(eventName, this.listener);
  }

  render() {
    return HtmlContents;
  }

  attributeChangedCallback(
    name: string,
    _old: string | null,
    value: string | null
  ) {
    // eslint-disable-next-line wc/guard-super-call
    super.attributeChangedCallback(name, _old, value);
    if (name === 'base-path' && value) {
      installBasePath(value);
    }
    if (name === 'client-source' && value) {
      installClientSource(value);
    }
  }

  protected createRenderRoot(): Element | ShadowRoot {
    return this;
  }
}
