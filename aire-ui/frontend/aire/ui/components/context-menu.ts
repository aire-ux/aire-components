import {customElement, html, LitElement} from "lit-element";


@customElement('aire-context-menu')
export class ContextMenu extends LitElement {

  static css = `
    :host {
    }
  `;
  protected render(): unknown {
    return html`
      <nav>
        <slot>
        </slot>
      </nav>
    `;
  }

  public open(location: Location) {
    this.style.display = 'flex';
  }

  public close() {
    this.style.display = 'none';
  }
}

type Location = {
  x: number;
  y: number;
}