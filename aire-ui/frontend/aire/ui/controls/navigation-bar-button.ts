import {css, customElement, html, LitElement} from "lit-element";

@customElement('aire-navigation-bar-button')
export class NavigationBarButton extends LitElement {

  static styles = css`
    button {
      width: 48px;
      height:48px;
      display: flex;
      background-color: inherit;
      align-items: center;
      border: none;
    }

  `;
  render() {
    return html`
      <button>
        <slot></slot>
      </button>
    `;
  }

}