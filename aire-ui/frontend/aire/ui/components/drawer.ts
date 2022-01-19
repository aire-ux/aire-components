import {css, customElement, html, LitElement} from "lit-element";

@customElement('aire-drawer')
export class Drawer extends LitElement {
  static styles = css`
    
    div.gutter {
      width:4px;
      background-color: #a2a2a2;
    }
    :host(.vertical) div {
      height:100%;
    }
    
    :host(.horizontal) div {
      width:100%;
      
    }
  `;

  render() {
    return html`
      <style>
        :host(.vertical) div.gutter {
          position: absolute;
          top: 0;
          left: ${this.parentElement?.clientWidth}px;
        }
      </style>
      <div class="gutter"></div>
    `;
  }
}