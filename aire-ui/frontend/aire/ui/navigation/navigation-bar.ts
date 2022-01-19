import {
  css,
  customElement,
  html,
  LitElement,
  PropertyValues
} from "lit-element";

@customElement('aire-navigation-bar')
export class NavigationBar extends LitElement {

  static styles = css`
    nav {
      width: 100%;
      height:100%;
      display: flex;
    }
    
    :host(.horizontal) nav {
      align-items: center;
      flex-direction: row-reverse;
    }
    
    :host(.vertical) {
      flex-direction:column;
    }
  `;

  constructor() {
    super();
  }

  protected firstUpdated(_changedProperties: PropertyValues) {
    super.firstUpdated(_changedProperties);
    // const root = this.shadowRoot,
    //     drawerSlot = root?.querySelector('slot[name="drawer"]');
    // // drawerSlot?.addEventListener('slotchange', (e) => {
    // //   console.log("Changed!" + e);
    // // });
  }

  protected render(): unknown {
    return html`
      <nav part="navigation">
        <slot>
        </slot>
        <slot name="drawer" part="drawer"></slot>
      </nav>
    `
  }
}