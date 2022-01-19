import {css, customElement, html, LitElement, query} from "lit-element";
import {Button} from "@vaadin/button";

@customElement('aire-navigation-bar-button')
export class NavigationBarButton extends LitElement {

  static styles = css`
    :host {
      display: block;
    }
    :host(.container-end) {
      position: absolute;
      left:0;
    }
    button {
      margin:0;
      padding:0;
      width: 48px;
      height:40px;
      background-color: inherit;
      border: none;
      line-height:40px;
      display: flex;
      justify-content: center;
      align-items: center;
      position: relative;
      box-sizing: border-box;
    }
    
    button:hover:before {
      content: "";
      position: absolute;
      height:40%;
      left:0px;
      border-left: 4px solid #2385af;
      border-radius: 1px;
    }
    
    button:focus ::slotted(vaadin-icon):before {
      content: "";
      position: absolute;
      height:24px;
      right:-5px;
      border-right: 5px solid #4d5e7c;
      z-index: 100;
    }
    
    button:focus ::slotted(vaadin-icon){
      /*color: white;*/
      color: #4d5e7c
    }
    
    button:focus  {
      /*background-color: #4d5e7c;*/
      border: 1px solid #4d5e7c;
      /*color: white;*/
    }
    
    ::slotted(img) {
      width: 32px;
      height:32px;
    }
    
    ::slotted(vaadin-icon) {
      width:16px;
      height: 16px;
      color: #2385af;
    }
    

  `;


  @query('button')
  private button: Button | undefined;


  private onButtonClicked(): void {
    const button = this.button;
    if (button) {
      button.focus();
    }
  }

  render() {
    return html`
      <button @click="${this.onButtonClicked}">
        <slot></slot>
      </button>
    `;
  }

}