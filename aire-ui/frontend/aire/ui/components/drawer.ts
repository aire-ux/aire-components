import {css, customElement, html, LitElement, property, PropertyValues, query} from "lit-element";

export enum State {
  Open = "open",
  Closed = "closed"
}

@customElement('aire-drawer')
export class Drawer extends LitElement {
  // language=CSS
  static styles = css`
    
    div.gutter {
      width:6px;
      background-color: #f2f2f2;
      display: flex;
      flex-direction: column;
      cursor: col-resize;
    }
    
    div.gutter > div.control {
      width:6px;
      height:24px;
      z-index: 100;
      margin-top: auto;
      margin-bottom:auto;
    }

    div.gutter > div.control > vaadin-icon {
      z-index: 102;
      left:-2px;
      position:absolute;
      color: #a366a3;
      width:9px;
    }

    :host(.vertical) div {
      height:100%;
    }
    
    :host(.horizontal) div {
      width:100%;
    }
    
    :host(.vertical) div.content {
      height: 100%;
      position: absolute;
      z-index: 101;
      top:0;
      box-sizing: border-box;
      background-color: white;
      /*border: 1px solid #4D5E7C;*/
    }
    
    
    :host(.verticalright) div.content {
      height: calc(100% - 2px);
      position: absolute;
      z-index: 101;
      top:0;
      box-sizing: border-box;
      background-color: white;
      border-left: 1px solid #AAAAAA;
    }
    
    
    
    :host(.verticalright) div.content.open {
      display: flex;
      width:350px;
      background-color: #f3f3f3;
      border-right: 1px solid #AAAAAA;
      right:30px;
      transition: left .5s, width .5s, background-color .5s;
    }
    
    :host(.verticalright) div.content.closed {
      width:0;
      background-color: unset;
      right:30px;
      transition: right .5s, width .5s, background-color .5s;
    }
    
    :host(.vertical) div.content.closed {
      width:0;
      background-color: unset;
      transition: width .5s, background-color .5s;
    }
    
    
    :host(.vertical) div.content.open {
      display: flex;
      width:250px;
      background-color: white;
      transition: width .5s, background-color .5s;
    }
  `;


  @property({
    type: String,
    reflect: true
  })
  public state: State;

  private previousState: State | undefined;

  constructor() {
    super();
    this.state = State.Closed;
  }

  @query('div.content')
  private content: HTMLDivElement | undefined;

  connectedCallback() {
    super.connectedCallback();
    this.closeDrawer();
    this.content?.addEventListener('transitionend', this.drawerAnimationLifecycleEventListener);
  }


  private drawerAnimationLifecycleEventListener = () => {
    if (this.state !== this.previousState) {
      this.dispatchEvent(this.createEvent());
      this.previousState = this.state;
    }
  }


  disconnectedCallback() {
    super.disconnectedCallback();
    this.content?.removeEventListener('transitionend', this.drawerAnimationLifecycleEventListener);
  }

  private createEvent(): CustomEvent {
    if (this.state === State.Closed) {
      return new CustomEvent('drawer-closed');
    } else {
      return new CustomEvent('drawer-opened');
    }
  }

  public openDrawer(): void {
    const content = this.content;
    if (content) {
      content.classList.add('open');
      content.classList.remove('closed');
      const previousState = this.state;
      this.state = State.Open;
      this.requestUpdate('state', previousState);
    }
  }


  public closeDrawer(): void {
    const content = this.content;
    if (content) {
      content.classList.add('closed');
      content.classList.remove('open');
      const previousState = this.state;
      this.state = State.Closed;
      this.requestUpdate('state', previousState);
    }
  }

  public toggle(): void {
    if (this.state == State.Open) {
      this.closeDrawer();
    } else {
      this.openDrawer();
    }
  }


  protected update(changedProperties: PropertyValues) {
    super.update(changedProperties);
  }


  attributeChangedCallback(name: string, _old: string | null, value: string | null) {
    super.attributeChangedCallback(name, _old, value);
    if (name === 'state') {
      if (value === State.Open) {
        this.openDrawer();
      } else {
        this.closeDrawer();

      }
    }
  }

  render() {
    return html`
      <style>
        :host(.vertical) div.gutter {
          position: absolute;
          top: 0;
          left: ${this.parentElement?.clientWidth}px;
        }

        :host(.verticalright) div.gutter {
          position: absolute;
          right: ${this.parentElement?.clientWidth}px;
          height: 100%;
          display: flex;
          flex-direction: column;
          top: ${this.parentElement?.clientTop}px;
        }

        :host(.vertical) div.content {
          left: ${(this.parentElement?.clientWidth as number) + 6}px;
        }
      </style>
      <div class="gutter">
        <div class="control" @click="${this.toggle}">
          <vaadin-icon icon="vaadin:ellipsis-v"></vaadin-icon>
        </div>
      </div>
      <div class="content">
        <slot></slot>
      </div>
    `;
  }
}