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
    
    div.active-container {
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