import {css, customElement, html, LitElement} from "lit-element";

@customElement('aire-overlay')
export class Overlay extends LitElement {

  static styles = css`
    :host {
      position: absolute;
      background-color: white;
      z-index: 1000;
      display: flex;
      flex-direction: column;
      
      /*border: 10px solid red;*/
    }
    ::slotted(header) {
      height:48px;
      display: flex;
      flex-direction: row;
      padding: 0 8px;
    }
    
    ::slotted(article) {
      flex-grow: 1;
      display: flex;
      flex-direction: column;
    }
    ::slotted(footer) {
      height:32px;
      display: flex;
      flex-direction: row;
    }
  
  `

  connectedCallback() {
    super.connectedCallback();
    window.addEventListener('resize', this.handleResize);
  }

  disconnectedCallback() {
    super.disconnectedCallback();
    window.removeEventListener('resize', this.handleResize);
  }


  handleResize = () => {
    const host = this.parentElement;
    if (host) {
      this.style.width = `${host.clientWidth}px`;
      this.style.height = `${host.clientHeight - 2}px`;
    }
  };

  render() {
    const host = this.parentElement;
    return html`
      <style>
        :host {
          top: 0px;
          left: 0px;
          width: ${host?.clientWidth}px;
          height: ${(host?.clientHeight || 0) - 2}px;
        }
      </style>
      <slot name="header" part="header"></slot>
      <slot name="content" part="content"></slot>
      <slot name="footer" part="footer"></slot>
    `
  }

}