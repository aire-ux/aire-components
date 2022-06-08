import {css, customElement, html, LitElement, property} from "lit-element";
import {HTMLTemplateResult} from "lit";

@customElement('aire-card')
export class Card extends LitElement {

  static styles = css`
    :host {
      display: inline-block;
      height:200px;
    }
    article {
      width: 196px;
      height: 132px;
      margin-top: 32px;
      margin-left:32px;
      border-radius: 8px;
      position: relative;
      display: flex;
      flex-direction: column;
      box-sizing: border-box;
      transition: box-shadow .3s;
      cursor: pointer;
    }
    
    div.icon {
      width: 40px;
      height: 40px;
      position: absolute;
      border-radius: 8px;
      top: -20px;
      left: 20px;
      z-index: 1;
      background-color: white;
      transition: box-shadow .3s;
      display: flex;
      align-items: center;
      justify-content: center;
    }
    
    ::slotted(header) {
      height:20px;
      margin-left:60px;
      line-height: 20px;
      vertical-align: middle;
      padding-left: .50em;
      font-size: var(--lumo-font-size-m);
      font-weight: 400;
      font-family: var(--lumo-font-family);
      color: var(--lumo-secondary-text-color);
    }
    
    ::slotted(footer) {
      height: 20px;
      padding: var(--lumo-space-xs) var(--lumo-space-s);
      display: flex;
      flex-direction: row-reverse;
    }
    
    ::slotted(section){
      flex: 1 1;
      padding: var(--lumo-space-s)
    }
  `

  @property({reflect: true, attribute: true})
  private color: string | undefined = '#AAAAAA';

  render(): HTMLTemplateResult {
    return html`
      <style>
        article {
          border: 1px solid ${this.color};
        }
        article:hover {
          box-shadow: 0 0 11px rgba(66,00,66,.3);
        }
        
        article:hover div.icon {
          box-shadow: 0 0 11px rgba(66,00,66,.3);
        }
        
        div.icon {
          border: 1px solid ${this.color}
          
        }
        div.icon > ::slotted(img) {
          max-width: 30px;
          max-height: 30px;
          width: 30px;
          height: 30px;
        }
        ::slotted(footer) {
          border-top: 1px solid ${this.color}
        }
      </style>
      
      <article part="content">
        <div class="icon">
          <slot name="icon"></slot>
        </div>
        
        <slot name="header" part="header"></slot>

        <slot name="content" part="content">

        </slot>
        <slot name="footer" part="footer">

        </slot>
      </article>

    `;
  }

}