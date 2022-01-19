import {css, customElement, html, LitElement, query} from "lit-element";

export enum State {
  Open,
  Closed
}

@customElement('aire-drawer')
export class Drawer extends LitElement {
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
      border: 1px solid #4D5E7C;
    }
    
    
    :host(.vertical) div.content.closed {
      width:0;
      border: 1px solid #4D5E7C;
      transition: width .5s;
    }
    :host(.vertical) div.content.open {
      display: flex;
      width:250px;
      transition: width .5s;
    }
  `;

  public state: State;

  constructor() {
    super();
    this.state = State.Closed;
  }

  @query('div.content')
  private content: HTMLDivElement | undefined;

  connectedCallback() {
    super.connectedCallback();
    this.closeDrawer();
  }

  disconnectedCallback() {
    super.disconnectedCallback();
  }

  public openDrawer(): void {
    const content = this.content;
    if (content) {
      content.classList.add('open');
      content.classList.remove('closed');
      this.state = State.Open;
    }
  }


  public closeDrawer(): void {
    const content = this.content;
    if (content) {
      content.classList.add('closed');
      content.classList.remove('open');
      this.state = State.Closed;
    }
  }

  public toggle(): void {
    if (this.state == State.Open) {
      this.closeDrawer();
    } else {
      this.openDrawer();
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