import {css, customElement, html, LitElement, PropertyValues} from "lit-element";
import {Dynamic, Receive, Remotable, Remote} from "@aire-ux/aire-condensation";

const OVERLAY_NAME = 'VAADIN-CONTEXT-MENU-OVERLAY';

@Remotable
@customElement('aire-context-menu')
export class ContextMenu extends LitElement {

  // language=CSS
  static styles = css`

    :host {
      background-color: white;
      box-shadow: var(--lumo-box-shadow-m);
      padding: 4px;
    }
  `;



  constructor() {
    super();
  }


  protected render(): unknown {
    return html`
      <nav part="navigation">
        <slot part="content">
        </slot>
      </nav>
    `;
  }


  connectedCallback() {
    super.connectedCallback();
    this.setAttribute('tabindex', '1');
    window.addEventListener('click', this.handleFocusLost);
  }

  disconnectedCallback() {
    super.disconnectedCallback();
    window.removeEventListener('click', this.handleFocusLost);
  }

  @Remote
  public open(@Receive(Dynamic) location: Location) {
    this.style.position = 'fixed';
    this.style.zIndex = '1000';
    this.style.top = location.y + 'px';
    this.style.left = location.x + 'px'
    this.requestUpdate();
  }


  protected updated(_changedProperties: PropertyValues) {
    super.updated(_changedProperties);
    this.dispatchEvent(new CustomEvent('context-menu-state-changed', {
      detail: {
        closed: false
      }
    }));
  }

  public close() {
    this.dispatchEvent(new CustomEvent('context-menu-state-changed', {
      detail: {
        state: true
      }
    }));
  }

  private handleFocusLost = (e: Event) => {
    this.close();
  }

}

type Location = {
  x: number;
  y: number;
}