import {css, customElement, html, LitElement} from "lit-element";

@customElement('aire-floating-action-button')
export class FloatingActionButton extends LitElement {


  // language=CSS
  static styles = css`
    button, button.primary {
      background-color: #370e8b;
      color: #fafafa;
      border-radius: 50%;
      width: 48px;
      height: 48px;
      border: none;
      position: fixed;
      bottom: 64px;
      right: 48px;
      z-index:100;
    }
    button:hover {
      background-color: #660066;
    }
    button:active {
      background-color: #a366a3;
    }
  
  `;

  render() {
    return html`
      <button>
        <slot name="icon"></slot>
      </button>
    `;

  }

}