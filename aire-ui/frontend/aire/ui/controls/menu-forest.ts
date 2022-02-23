import {css, customElement, html, LitElement, PropertyValues, query} from "lit-element";


@customElement('aire-menu-forest')
export class MenuForest extends LitElement {


  static styles = css`
    :host {
      display: block;
      position: absolute;
      background-color: white;
      box-shadow: var(--lumo-box-shadow-xs);
    }
    :host > nav {
      display: flex;
      min-width:128px;
      flex-direction: column;
    }
    
    ::slotted(vaadin-button[subcontent])::after {
      display: inline-block;
      width:10px;
      background-repeat: no-repeat;
      background-image: url("data:image/svg+xml,%3Csvg xmlns='http://www.w3.org/2000/svg' xmlns:xlink='http://www.w3.org/1999/xlink' version='1.1' preserveAspectRatio='xMidYMid meet' aria-hidden='true' viewBox='0 0 16 16'%3E%3C!----%3E%3C!--%3Flit$6191637496$--%3E%3Cpath d='M13.1 8l-8 8-2.2-2.1 5.9-5.9-5.9-5.9 2.2-2.1z' style='&%2310; fill: %234D5E7c;&%2310;'/%3E%3C!--%3F--%3E%3C/svg%3E");
      filter: unset;
      transition: unset;
      opacity: 100%;
      background-color: unset;
      vertical-align: middle;
      line-height: 100%;
      margin: 0 2px 0 calc(100% - 20px);
      top: calc(50% - 4px);
    }
  `;

  @query('div.active-container')
  private container: HTMLDivElement | undefined;


  get host(): HTMLElement | null {
    const parent = this.parentElement;
    if (parent) {
      let host = parent.querySelector('[host]');
      if (host) {
        return host as HTMLElement;
      }
    }
    return null;
  }


  protected firstUpdated(_changedProperties: PropertyValues) {
    super.firstUpdated(_changedProperties);
    const host = this.host,
        container = this.container;
    if (host && container) {
      const rect = host.getBoundingClientRect();
      this.style.position = 'fixed';
      this.style.top = (rect.top - 4) + 'px';
      this.style.left = rect.right + 'px';
    }
  }

  disconnectedCallback() {
    super.disconnectedCallback();
  }

  protected render(): unknown {
    return html`
      <nav>
        <slot part="header" name="header"></slot>
        <slot part="primary"></slot>
        <slot part="footer" name="footer"></slot>
      </nav>
      <div part="active-container" class="active-container">
        <slot part="active" name="active"></slot>
      </div>
    `;
  }

}